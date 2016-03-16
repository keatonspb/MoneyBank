package keaton.moneybank;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class MoneyBankWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.money_bank_widget);
        //Подготавливаем Intent для Broadcast
        Intent expence =  new Intent(context, ReportActivity.class);
        Intent income = new Intent(context, ReportActivity.class);
        income.setAction(ReportActivity.ACTION_SHOW_INCOME);



        //создаем наше событие
        PendingIntent expencePendingIntent = PendingIntent.getActivity(context, 0, expence, 0);
        PendingIntent incomePendingIntent = PendingIntent.getActivity(context, 0, income, 0);


        views.setOnClickPendingIntent(R.id.make_expence_widget, expencePendingIntent);
        views.setOnClickPendingIntent(R.id.make_income_widget, incomePendingIntent);





        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

