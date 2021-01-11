# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

terraform {
  required_version = "~> 0.12.0"
  required_providers {
    google      = "~> 3.0"
    google-beta = "~> 3.0"
  }
  backend "gcs" {
    bucket = "mystudies-dev1-terraform-state"
    prefix = "mystudies-dev1-networks"
  }
}

resource "google_compute_firewall" "fw_allow_k8s_ingress_lb_health_checks" {
  name        = "fw-allow-k8s-ingress-lb-health-checks"
  description = "GCE L7 firewall rule"
  network     = module.mystudies_dev1_network.network.network.self_link
  project     = module.project.project_id

  allow {
    protocol = "tcp"
    ports    = ["30000-32767"]
  }
  allow {
    protocol = "tcp"
    ports    = ["4444"]
  }
  allow {
    protocol = "tcp"
    ports    = ["80"]
  }
  allow {
    protocol = "tcp"
    ports    = ["8080"]
  }

  # Load Balancer Health Check IP ranges.
  source_ranges = [
    "130.211.0.0/22",
    "209.85.152.0/22",
    "209.85.204.0/22",
    "35.191.0.0/16",
  ]

  target_tags = [
    "gke-mystudies-dev1-gke-cluster",
    "gke-mystudies-dev1-gke-cluster-default-node-pool",
  ]
}

# Create the project and optionally enable APIs, create the deletion lien and add to shared VPC.
# Deletion lien: https://cloud.google.com/resource-manager/docs/project-liens
# Shared VPC: https://cloud.google.com/docs/enterprise/best-practices-for-enterprise-organizations#centralize_network_control
module "project" {
  source  = "terraform-google-modules/project-factory/google"
  version = "~> 9.1.0"

  name                    = "mystudies-dev1-networks"
  org_id                  = ""
  folder_id               = "296946598967"
  billing_account         = "00584D-616AD1-DBFCA2"
  lien                    = true
  default_service_account = "keep"
  skip_gcloud_download    = true
  activate_apis = [
    "compute.googleapis.com",
    "container.googleapis.com",
    "iap.googleapis.com",
    "servicenetworking.googleapis.com",
    "sqladmin.googleapis.com",
  ]
}

resource "google_compute_shared_vpc_host_project" "host" {
  project = module.project.project_id
}

module "bastion_vm" {
  source  = "terraform-google-modules/bastion-host/google"
  version = "~> 2.10.0"

  name         = "bastion-vm"
  project      = module.project.project_id
  zone         = "asia-northeast1-b"
  host_project = module.project.project_id
  network      = module.mystudies_dev1_network.network.network.self_link
  subnet       = module.mystudies_dev1_network.subnets["asia-northeast1/mystudies-dev1-bastion-subnet"].self_link
  members      = ["group:mystudies-dev1-bastion-accessors@jcloudce.com"]
  image_family = "ubuntu-2004-lts"

  image_project = "ubuntu-os-cloud"




  startup_script = <<EOF
sudo apt-get -y update
sudo apt-get -y install mysql-client
sudo wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O /usr/local/bin/cloud_sql_proxy
sudo chmod +x /usr/local/bin/cloud_sql_proxy

EOF
}

module "mystudies_dev1_network" {
  source  = "terraform-google-modules/network/google"
  version = "~> 2.5.0"

  network_name = "mystudies-dev1-network"
  project_id   = module.project.project_id

  subnets = [
    {
      subnet_name           = "mystudies-dev1-bastion-subnet"
      subnet_ip             = "10.0.128.0/24"
      subnet_region         = "asia-northeast1"
      subnet_flow_logs      = true
      subnet_private_access = true
    },

    {
      subnet_name           = "mystudies-dev1-gke-subnet"
      subnet_ip             = "10.0.0.0/17"
      subnet_region         = "asia-northeast1"
      subnet_flow_logs      = true
      subnet_private_access = true
    },

  ]
  secondary_ranges = {
    "mystudies-dev1-gke-subnet" = [
      {
        range_name    = "mystudies-dev1-pods-range"
        ip_cidr_range = "172.16.0.0/14"
      },
      {
        range_name    = "mystudies-dev1-services-range"
        ip_cidr_range = "172.20.0.0/14"
      },
    ],
  }
}

module "cloud_sql_private_service_access_mystudies_dev1_network" {
  source  = "GoogleCloudPlatform/sql-db/google//modules/private_service_access"
  version = "~> 4.1.0"

  project_id  = module.project.project_id
  vpc_network = module.mystudies_dev1_network.network_name
}

module "mystudies_dev1_router" {
  source  = "terraform-google-modules/cloud-router/google"
  version = "~> 0.2.0"

  name    = "mystudies-dev1-router"
  project = module.project.project_id
  region  = "asia-northeast1"
  network = module.mystudies_dev1_network.network.network.self_link

  nats = [
    {
      name                               = "mystudies-dev1-nat"
      source_subnetwork_ip_ranges_to_nat = "LIST_OF_SUBNETWORKS"

      subnetworks = [
        {
          name                     = "${module.mystudies_dev1_network.subnets["asia-northeast1/mystudies-dev1-bastion-subnet"].self_link}"
          source_ip_ranges_to_nat  = ["PRIMARY_IP_RANGE"]
          secondary_ip_range_names = []
        },
        {
          name                     = "${module.mystudies_dev1_network.subnets["asia-northeast1/mystudies-dev1-gke-subnet"].self_link}"
          source_ip_ranges_to_nat  = ["ALL_IP_RANGES"]
          secondary_ip_range_names = []
        },
      ]
    },
  ]
}
