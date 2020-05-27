/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.notificationmodule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.harvard.R;
import com.harvard.notificationmodule.model.NotificationDb;
import com.harvard.storagemodule.DBServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.studymodel.NotificationDbResources;
import com.harvard.studyappmodule.studymodel.PendingIntentsResources;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmResults;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class NotificationModuleSubscriber {
  private static final String ACTIVITY = "activity";
  static final String NO_USE_NOTIFICATION = "noUseNotification";
  static final String NOTIFICATION_TURN_OFF_NOTIFICATION = "notificationTurnOffNotification";
  private static final String RESOURCES = "resources";
  private DBServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private int pendingId = 214747;
  private int pendingId1 = 214746;

  public NotificationModuleSubscriber(DBServiceSubscriber dbServiceSubscriber, Realm realmobj) {
    this.dbServiceSubscriber = dbServiceSubscriber;
    realm = realmobj;
  }

  private Date removeOffset(Date date, int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
    return calendar.getTime();
  }

  public void generateActivityLocalNotification(
      ActivityRun activityRun, Context context, String type, int offset) {
    String title = context.getResources().getString(R.string.app_name);
    String description = "";
    String description1 = "";
    SimpleDateFormat notificationFormat = AppController.getNotificationDateFormat();
    Calendar time = Calendar.getInstance();
    Calendar time1 = Calendar.getInstance();
    ActivitiesWS activitiesWS =
        dbServiceSubscriber.getActivityItem(
            activityRun.getStudyId(), activityRun.getActivityId(), realm);

    try {
      if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)) {
        Date date = removeOffset(activityRun.getEndDate(), offset);
        time.setTime(date);
        time.add(Calendar.HOUR_OF_DAY, -24);

        Date date1 = removeOffset(activityRun.getStartDate(), offset);
        time1.setTime(date1);

        description =
            context.getResources().getString(R.string.the_activity)
                + " "
                + activitiesWS.getTitle()
                + " "
                + context.getResources().getString(R.string.participation_is_important);
        description1 =
            context.getResources().getString(R.string.the_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.now_available_to_take);
      } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
        Date date = removeOffset(activityRun.getStartDate(), offset);
        time.setTime(date);
        description =
            context.getResources().getString(R.string.scheduled_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.valid_until)
                + " "
                + notificationFormat.format(activityRun.getEndDate())
                + context.getResources().getString(R.string.participation_is_important2);
      } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WEEKLY)) {
        Date date = removeOffset(activityRun.getEndDate(), offset);
        time.setTime(date);
        time.add(Calendar.HOUR_OF_DAY, -24);

        Date date1 = removeOffset(activityRun.getStartDate(), offset);
        time1.setTime(date1);

        description =
            context.getResources().getString(R.string.weekly_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.participation_is_important);
        description1 =
            context.getResources().getString(R.string.new_run)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.study_complete);
      } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MONTHLY)) {
        Date date = removeOffset(activityRun.getEndDate(), offset);
        time.setTime(date);
        time.add(Calendar.HOUR_OF_DAY, -72);

        Date date1 = removeOffset(activityRun.getStartDate(), offset);
        time1.setTime(date1);

        description =
            context.getResources().getString(R.string.monthly_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.expire_in_three_days);
        description1 =
            context.getResources().getString(R.string.new_run_monthly_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.study_complete);
      } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_DAILY)) {
        Date date = removeOffset(activityRun.getStartDate(), offset);
        time.setTime(date);
        description =
            context.getResources().getString(R.string.new_run_daily_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.your_participation_important);
      } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WITHIN_A_DAY)) {
        Date date = removeOffset(activityRun.getStartDate(), offset);
        time.setTime(date);
        description =
            context.getResources().getString(R.string.new_run_daily_activity)
                + " "
                + activitiesWS.getTitle()
                + ", "
                + context.getResources().getString(R.string.valid_until)
                + " "
                + notificationFormat.format(activityRun.getEndDate())
                + context.getResources().getString(R.string.participation_is_important2);
      }

      int notificationId;

      if (time.getTime().after(new Date())) {
        notificationId = new Random().nextInt();
        NotificationDb notificationDb = new NotificationDb();
        notificationDb.setStudyId(activityRun.getStudyId());
        notificationDb.setActivityId(activityRun.getActivityId());
        notificationDb.setNotificationId(notificationId);
        notificationDb.setDateTime(time.getTime());
        notificationDb.setType(ACTIVITY);
        notificationDb.setId(1);
        notificationDb.setTitle(title);
        notificationDb.setDescription(description);
        notificationDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
        dbServiceSubscriber.updateNotificationToDb(context, notificationDb);

        set24hourScheduler(context);
        if (isSameDay(new Date(), time.getTime())) {
          setAlarm(
              context,
              title,
              description,
              ACTIVITY,
              notificationId,
              activityRun.getStudyId(),
              activityRun.getActivityId(),
              time);
        }
      }

      // Notification availability for monthly, weekly, One time
      if ((type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MONTHLY)
          || type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WEEKLY)
          || type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME))) {
        if (time1.getTime().after(new Date())) {
          notificationId = new Random().nextInt();
          NotificationDb notificationDb = new NotificationDb();
          notificationDb.setStudyId(activityRun.getStudyId());
          notificationDb.setActivityId(activityRun.getActivityId());
          notificationDb.setNotificationId(notificationId);
          notificationDb.setDateTime(time1.getTime());
          notificationDb.setType(ACTIVITY);
          notificationDb.setTitle(title);
          notificationDb.setDescription(description1);
          notificationDb.setId(1);
          notificationDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
          dbServiceSubscriber.updateNotificationToDb(context, notificationDb);
          set24hourScheduler(context);
          if (isSameDay(new Date(), time1.getTime())) {
            setAlarm(
                context,
                title,
                description1,
                ACTIVITY,
                notificationId,
                activityRun.getStudyId(),
                activityRun.getActivityId(),
                time1);
          }
        }
      }

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void set24hourScheduler(Context context) {
    // checking if alram is working with pendingIntent
    boolean isWorking = false;
    try {
      Intent notificationIntent1 = new Intent(context, AlarmReceiver.class);
      notificationIntent1.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent1.addCategory("android.intent.category.DEFAULT");
      notificationIntent1.putExtra("pendingIntentId", 1);
      isWorking =
          (PendingIntent.getBroadcast(context, 1, notificationIntent1, PendingIntent.FLAG_NO_CREATE)
              != null); // just changed the flag
    } catch (Exception e) {
      Logger.log(e);
    }

    if (!isWorking) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      calendar.add(Calendar.DATE, 1);

      try {
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("pendingIntentId", 1);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent broadcast =
            PendingIntent.getBroadcast(
                context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
          } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
          }
        } catch (NullPointerException e) {
          Logger.log(e);
        }
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  private boolean isSameDay(Date date1, Date date2) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(date1);
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(date2);
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
        && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
        && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
  }

  public void setAlarm(
      Context context,
      String title,
      String description,
      String type,
      int notificationId,
      String studyId,
      String activityId,
      Calendar time) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    int notificationNumber = 1;
    Intent notificationIntent = new Intent(context, AlarmReceiver.class);
    notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
    notificationIntent.addCategory("android.intent.category.DEFAULT");
    notificationIntent.putExtra("title", title);
    notificationIntent.putExtra("description", description);
    notificationIntent.putExtra("type", type);
    notificationIntent.putExtra("notificationId", notificationId);
    notificationIntent.putExtra("studyId", studyId);
    notificationIntent.putExtra("activityId", activityId);
    notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
    notificationIntent.putExtra("notificationNumber", notificationNumber);
    try {
      int pendingIntentId =
          Integer.parseInt(
                  AppController.getHelperSharedPreference()
                      .readPreference(
                          context, context.getResources().getString(R.string.pendingCount), "5"))
              + 1;
      AppController.getHelperSharedPreference()
          .writePreference(
              context,
              context.getResources().getString(R.string.pendingCount),
              "" + pendingIntentId);

      notificationIntent.putExtra("pendingIntentId", pendingIntentId);

      PendingIntent broadcast =
          PendingIntent.getBroadcast(
              context, pendingIntentId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      PendingIntents pendingIntents = new PendingIntents();
      pendingIntents.setActivityId(activityId);
      pendingIntents.setStudyId(studyId);
      pendingIntents.setPendingIntentId(pendingIntentId);
      pendingIntents.setDescription(description);
      pendingIntents.setTitle(title);
      pendingIntents.setType(type);
      pendingIntents.setNotificationId(notificationId);

      dbServiceSubscriber.savePendingIntentId(context, pendingIntents);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
      } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void generateTwoWeekNotification(Date date, Context context) {
    try {

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      String title = context.getResources().getString(R.string.app_name);
      Intent notificationIntent = new Intent(context, AlarmReceiver.class);
      notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent.addCategory("android.intent.category.DEFAULT");
      notificationIntent.putExtra("title", title);
      notificationIntent.putExtra(
          "description", context.getResources().getString(R.string.studie_your_enrolled));
      notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
      notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

      PendingIntent broadcast =
          PendingIntent.getBroadcast(
              context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
      } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void cancelTwoWeekNotification(Context context) {
    try {

      String title = context.getResources().getString(R.string.app_name);
      Intent notificationIntent = new Intent(context, AlarmReceiver.class);
      notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent.addCategory("android.intent.category.DEFAULT");
      notificationIntent.putExtra("title", title);
      notificationIntent.putExtra(
          "description", context.getResources().getString(R.string.studie_your_enrolled));
      notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
      PendingIntent broadcast =
          PendingIntent.getBroadcast(
              context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      broadcast.cancel();
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmManager.cancel(broadcast);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void generateNotificationTurnOffNotification(Date date, Context context) {
    try {

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(Calendar.DAY_OF_MONTH, 7);
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      String title = context.getResources().getString(R.string.app_name);
      Intent notificationIntent = new Intent(context, AlarmReceiver.class);
      notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent.addCategory("android.intent.category.DEFAULT");
      notificationIntent.putExtra("title", title);
      notificationIntent.putExtra(
          "description", context.getResources().getString(R.string.notificatinturnoffnotification));
      notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
      notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

      PendingIntent broadcast =
          PendingIntent.getBroadcast(
              context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
      } else {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void cancelNotificationTurnOffNotification(Context context) {
    try {

      String title = context.getResources().getString(R.string.app_name);
      Intent notificationIntent = new Intent(context, AlarmReceiver.class);
      notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent.addCategory("android.intent.category.DEFAULT");
      notificationIntent.putExtra("title", title);
      notificationIntent.putExtra(
          "description", context.getResources().getString(R.string.notificatinturnoffnotification));
      notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
      PendingIntent broadcast =
          PendingIntent.getBroadcast(
              context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      broadcast.cancel();
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmManager.cancel(broadcast);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void generateAnchorDateLocalNotification(
      Date date,
      String activityId,
      String studyId,
      Context context,
      String notificationTest,
      String resourceId) {

    String description = "";

    NotificationDbResources notificationsDb = null;
    RealmResults<NotificationDbResources> notificationsDbs =
        dbServiceSubscriber.getNotificationDbResources(activityId, studyId, RESOURCES, realm);
    int id = 0;
    if (notificationsDbs != null && notificationsDbs.size() > 0) {
      for (int i = 0; i < notificationsDbs.size(); i++) {
        if (notificationsDbs.get(i).getResourceId().equalsIgnoreCase(resourceId)) {
          notificationsDb = notificationsDbs.get(i);
        }
        id = notificationsDbs.get(i).getId();
      }
    }
    id++;
    description = notificationTest;
    Calendar time = Calendar.getInstance();
    time.setTime(date);
    String title = "MyStudies";
    int notificationId = new Random().nextInt(10000);
    NotificationDbResources notificationDb = new NotificationDbResources();
    notificationDb.setStudyId(studyId);
    notificationDb.setActivityId(activityId);
    notificationDb.setNotificationId(notificationId);
    notificationDb.setType(RESOURCES);
    notificationDb.setDateTime(time.getTime());
    notificationDb.setResourceId(resourceId);
    notificationDb.setTitle(title);
    notificationDb.setDescription(description);
    notificationDb.setId(id);
    if (notificationsDb == null) {
      dbServiceSubscriber.updateNotificationToDb(context, notificationDb);
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      Intent notificationIntent = new Intent(context, AlarmReceiver.class);
      notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
      notificationIntent.addCategory("android.intent.category.DEFAULT");
      notificationIntent.putExtra("title", title);
      notificationIntent.putExtra("description", description);
      notificationIntent.putExtra("type", RESOURCES);
      notificationIntent.putExtra("studyId", studyId);
      notificationIntent.putExtra("notificationId", notificationId);
      notificationIntent.putExtra("activityId", activityId);
      notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
      int notificationNumber = 1;
      notificationIntent.putExtra("notificationNumber", notificationNumber);
      try {
        int pendingIntentId =
            Integer.parseInt(
                    AppController.getHelperSharedPreference()
                        .readPreference(
                            context,
                            context.getResources().getString(R.string.pendingCountResources),
                            "0"))
                + 1;
        AppController.getHelperSharedPreference()
            .writePreference(
                context,
                context.getResources().getString(R.string.pendingCountResources),
                "" + pendingIntentId);
        PendingIntent broadcast =
            PendingIntent.getBroadcast(
                context, pendingIntentId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntentsResources pendingIntents = new PendingIntentsResources();
        pendingIntents.setActivityId(activityId);
        pendingIntents.setStudyId(studyId);
        pendingIntents.setPendingIntentId(pendingIntentId);

        pendingIntents.setTitle(title);
        pendingIntents.setType(description);
        pendingIntents.setDescription(RESOURCES);
        pendingIntents.setNotificationId(notificationId);

        dbServiceSubscriber.savePendingIntentIdResources(context, pendingIntents);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          alarmManager.setExactAndAllowWhileIdle(
              AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
        } else {
          alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
        }
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  public void cancleResourcesLocalNotification(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    RealmResults<PendingIntentsResources> pendingIntentses =
        dbServiceSubscriber.getPendingIntentIdResources(realm);
    if (pendingIntentses != null) {
      for (int i = 0; i < pendingIntentses.size(); i++) {
        try {
          Intent notificationIntent = new Intent(context, AlarmReceiver.class);
          notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
          notificationIntent.addCategory("android.intent.category.DEFAULT");
          notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
          notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
          notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
          notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
          notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
          notificationIntent.putExtra(
              "notificationId", pendingIntentses.get(i).getNotificationId());
          PendingIntent broadcast =
              PendingIntent.getBroadcast(
                  context,
                  pendingIntentses.get(i).getPendingIntentId(),
                  notificationIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);
          broadcast.cancel();
          alarmManager.cancel(broadcast);
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    }
  }

  public void cancleResourcesLocalNotificationByIds(
      Context context, String activityId, String studyId) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    RealmResults<PendingIntentsResources> pendingIntentses =
        dbServiceSubscriber.getPendingIntentIdResourcesByIds(realm, activityId, studyId);
    if (pendingIntentses != null) {
      for (int i = 0; i < pendingIntentses.size(); i++) {
        try {
          Intent notificationIntent = new Intent(context, AlarmReceiver.class);
          notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
          notificationIntent.addCategory("android.intent.category.DEFAULT");
          notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
          notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
          notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
          notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
          notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
          notificationIntent.putExtra(
              "notificationId", pendingIntentses.get(i).getNotificationId());
          PendingIntent broadcast =
              PendingIntent.getBroadcast(
                  context,
                  pendingIntentses.get(i).getPendingIntentId(),
                  notificationIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);
          broadcast.cancel();
          alarmManager.cancel(broadcast);
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    }
  }

  public void cancleActivityLocalNotificationByIds(
      Context context, String activityId, String studyId) {
    try {

      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      RealmResults<PendingIntents> pendingIntentses =
          dbServiceSubscriber.getPendingIntentIdByIds(realm, activityId, studyId);
      if (pendingIntentses != null) {
        for (int i = 0; i < pendingIntentses.size(); i++) {
          Intent notificationIntent = new Intent(context, AlarmReceiver.class);
          notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
          notificationIntent.addCategory("android.intent.category.DEFAULT");
          notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
          notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
          notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
          notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
          notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
          notificationIntent.putExtra(
              "notificationId", pendingIntentses.get(i).getNotificationId());
          PendingIntent broadcast =
              PendingIntent.getBroadcast(
                  context,
                  pendingIntentses.get(i).getPendingIntentId(),
                  notificationIntent,
                  PendingIntent.FLAG_UPDATE_CURRENT);
          broadcast.cancel();
          alarmManager.cancel(broadcast);
        }
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public void cancleActivityLocalNotification(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    RealmResults<PendingIntents> pendingIntentses = dbServiceSubscriber.getPendingIntentId(realm);
    if (pendingIntentses != null) {
      for (int i = 0; i < pendingIntentses.size(); i++) {
        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
        notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
        notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
        notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
        notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
        notificationIntent.putExtra("notificationId", pendingIntentses.get(i).getNotificationId());
        PendingIntent broadcast =
            PendingIntent.getBroadcast(
                context,
                pendingIntentses.get(i).getPendingIntentId(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        broadcast.cancel();
        alarmManager.cancel(broadcast);
      }
    }
  }
}
