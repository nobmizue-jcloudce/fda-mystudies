<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div class="col-sm-10 col-rc white-bg p-none">

  <!--  Start top tab section-->
  <div class="right-content-head">
    <div class="text-right">
      <div class="black-md-f text-uppercase dis-line pull-left line34">ACTIONS</div>
      <div class="dis-line form-group mb-none mr-sm"></div>
      <div class="dis-line form-group mb-none"></div>
    </div>
  </div>
  <!--  End  top tab section-->
  <!--  Start body tab section -->
  <div class="right-content-body">
    <div>
      <c:if test="${studyBo.studyPreActiveFlag eq false}">
        <div class="form-group mr-sm" style="white-space: normal;">
          <button type="button" class="btn btn-primary blue-btn-action"
                  id="publishId" onclick="validateStudyStatus(this);"
              <c:choose>
                <c:when test="${not empty permission}">
                  disabled
                </c:when>
                <c:when
                    test="${not empty studyBo.status && (studyBo.status eq 'Paused' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Active' || studyBo.status eq 'Deactivated')}">
                  disabled
                </c:when>
              </c:choose>
                  <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Publish as
            Upcoming
            Study
          </button>
        </div>
      </c:if>
      <c:if test="${studyBo.studyPreActiveFlag eq true}">
        <div class="form-group mr-sm" style="white-space: normal;">
          <button type="button" class="btn btn-primary blue-btn-action"
                  id="unpublishId" onclick="validateStudyStatus(this);"
              <c:choose>
                <c:when test="${not empty permission}">
                  disabled
                </c:when>
                <c:when
                    test="${not empty studyBo.status && (studyBo.status eq 'Paused' || studyBo.status eq 'Active' || studyBo.status eq 'Deactivated')}">
                  disabled
                </c:when>
              </c:choose>
                  <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Unpublish
          </button>
        </div>
      </c:if>
      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="lunchId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${not empty studyBo.status && (studyBo.status eq 'Active' || studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Launch
          Study
        </button>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action"
                id="updatesId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when test="${not empty studyBo.status && empty liveStudyBo && (studyBo.hasStudyDraft==0  || studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' ||
					             studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when test="${not empty studyBo.status && not empty liveStudyBo && (studyBo.hasStudyDraft==0  || studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' ||
					             studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated' || liveStudyBo.status eq 'Paused')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Publish
          Updates
        </button>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="pauseId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${empty liveStudyBo && not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Paused'  || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when
                  test="${not empty liveStudyBo && not empty liveStudyBo.status && (liveStudyBo.status eq 'Pre-launch' || liveStudyBo.status eq 'Pre-launch(Published)' || liveStudyBo.status eq 'Paused'  || liveStudyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Pause
        </button>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="resumeId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${empty liveStudyBo && not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Active' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when
                  test="${not empty liveStudyBo && not empty liveStudyBo.status && (liveStudyBo.status eq 'Pre-launch' || liveStudyBo.status eq 'Pre-launch(Published)' || liveStudyBo.status eq 'Active'  || liveStudyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Resume
        </button>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default red-btn-action "
                id="deactivateId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Deactivate
        </button>
      </div>
    </div>
  </div>
</div>
<form:form
    action="/studybuilder/adminStudies/updateStudyAction.do?_S=${param._S}"
    name="actionInfoForm" id="actionInfoForm" method="post">
  <input type="hidden" name="studyId" id="studyId" value="${studyBo.id}"/>
  <input type="hidden" name="buttonText" id="buttonText" value=""/>
</form:form>
<form:form
    action="/studybuilder/adminStudies/studyList.do?_S=${param._S}"
    name="studyListInfoForm" id="studyListInfoForm" method="post">
</form:form>
<script type="text/javascript">
  $(document).ready(function () {
    $(".menuNav li").removeClass('active');
    $(".tenth").addClass('active');
    $("#createStudyId").show();
    $('.tenth').removeClass('cursor-none');
  });

  function validateStudyStatus(obj) {
    var buttonText = obj.id;
    var messageText = "";
    if (buttonText) {
      if (buttonText == 'unpublishId' || buttonText == 'pauseId'
          || buttonText == 'deactivateId') {
        if (buttonText == 'unpublishId') {
          messageText = "You are attempting to Unpublish the study. Are you sure you wish to proceed?";
        } else if (buttonText == 'pauseId') {
          messageText = "You are attempting to Pause the study. Mobile app users can no longer participate in study activities until you resume the study again. However, they will still be able to view the study dashboard and study resources. Are you sure you wish to proceed?";
        } else if (buttonText == 'deactivateId') {
          messageText = "You are attempting to Deactivate a live study. Once deactivated, mobile app users will no longer be able to participate in the study. Also, deactivated studies can never be reactivated. Are you sure you wish to proceed?";
        }
        bootbox.confirm({
          closeButton: false,
          message: messageText,
          buttons: {
            'cancel': {
              label: 'Cancel',
            },
            'confirm': {
              label: 'OK',
            },
          },
          callback: function (result) {
            if (result) {
              updateStudyByAction(buttonText);
            }
          }
        });
      } else {
        $
            .ajax({
              url: "/studybuilder/adminStudies/validateStudyAction.do?_S=${param._S}",
              type: "POST",
              datatype: "json",
              data: {
                buttonText: buttonText,
                "${_csrf.parameterName}": "${_csrf.token}",
              },
              success: function emailValid(data, status) {
                var message = data.message;
                var checkListMessage = data.checkListMessage;
                var checkFailureMessage = data.checkFailureMessage;
                if (message == "SUCCESS") {
                	showBootBoxMessage(buttonText,
                            messageText);
                } else {
                  if (buttonText == 'publishId') {
                    messageText = "The Basic Information, Settings, Overview and Consent sections must be marked complete before publishing a study as upcoming. Please complete these sections and try again.";
                  } else if (buttonText == 'lunchId') {
                    messageText = "All Study Builder sections must be marked complete to publish the study. Please complete these sections to proceed.";
                  } else if (buttonText == 'updatesId') {
                    messageText = "All Study Builder sections must be marked complete to publish updates to the study. Please complete these sections to proceed.";
                  }
                  bootbox.confirm(message, function (result) {
                    bootbox.alert(messageText);
                  })

                }
              },
              error: function status(data, status) {
                $("body").removeClass("loading");
              },
              complete: function () {
                $('.actBut').removeAttr('disabled');
              }
            });
      }
    }

  }

  function showErrMsg1(message) {
    $("#alertMsg").removeClass('s-box').addClass('e-box').text(message);
    $('#alertMsg').show('10000');
    setTimeout(hideDisplayMessage, 10000);
  }

  function showBootBoxMessage(buttonText, messageText) {
    if (buttonText == 'resumeId') {
      messageText = "Resuming the study will allow mobile app users to resume participation. Would you like to proceed?";
    } else if (buttonText == 'publishId') {
      messageText = "Publishing the study as \"upcoming\" will enable mobile app users to see the study overview and consent form, but will not allow enrollment. Would you like to proceed?";
    } else if (buttonText == 'lunchId') {
      messageText = "Launching the study will allow mobile application users to see the study and start participating. Would you like to proceed?";
    } else if (buttonText == 'updatesId') {
      messageText = "Publishing study updates will allow mobile application users to see the changes to the study. Would you like to proceed?";
    }
    bootbox.confirm({
      closeButton: false,
      message: messageText,
      buttons: {
        'cancel': {
          label: 'Cancel',
        },
        'confirm': {
          label: 'OK',
        },
      },
      callback: function (result) {
        if (result) {
          updateStudyByAction(buttonText);
        }
      }
    })
  }

  function updateStudyByAction(buttonText) {
    if (buttonText) {
      var studyId = "${studyBo.id}";
      $
          .ajax({
            url: "/studybuilder/adminStudies/updateStudyAction.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              buttonText: buttonText,
              studyId: studyId,
              "${_csrf.parameterName}": "${_csrf.token}",
            },
            success: function updateAction(data, status) {
              var message = data.message;
              if (message == "SUCCESS") {
                if (buttonText == 'deactivateId'
                    || buttonText == 'lunchId'
                    || buttonText == 'updatesId') {
                  $('#studyListInfoForm').submit();
                } else {
                  document.studyListInfoForm.action = "/studybuilder/adminStudies/actionList.do?_S=${param._S}";
                  document.studyListInfoForm.submit();
                }
              } else {
                $('#studyListInfoForm').submit();
              }
            },
            error: function status(data, status) {
              $("body").removeClass("loading");
            },
            complete: function () {
              $('.actBut').removeAttr('disabled');
            }
          });
    }
  }
</script>