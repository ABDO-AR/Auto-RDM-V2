package com.ar.team.company.app.autordm.ar.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ar.team.company.app.autordm.model.Chat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This is the default class for any utils or helping things
 * like static methods or fields
 * you can put here all your utilities.
 */
@SuppressWarnings("unused")
public class ARUtils {

    // Method(Replay):
    public static void reply(Context context, Notification.Action action, String message) {
        // Catching The Error:
        try {
            // Initializing:
            Intent sendIntent = new Intent();
            Bundle msg = new Bundle();
            // ForOnActionsRemoteInput Because We Don't Know The Action Name:
            try {
                // Looping:
                for (RemoteInput input : action.getRemoteInputs()) {
                    msg.putCharSequence(input.getResultKey(), message);
                }
                // AddingResultsToIntent:
                RemoteInput.addResultsToIntent(action.getRemoteInputs(), sendIntent, msg);
                // SendingTheAction:
                action.actionIntent.send(context, 0, sendIntent);
            } catch (NullPointerException e) {
                Log.d("ARUtils: ", e.toString());
                Log.d("ARUtils: ", "Attempt to get length of null array");
            }
        } catch (Exception e) {
            // Printing Error To Console If There AreOne:
            e.printStackTrace();
        }
    }

    // For running new task intent:
    public static Intent runNewTask(Context context, Activity activity, Class<?> cls) {
        // OverrideAnimations:
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // Initializing:
        Intent newTask = new Intent(context, cls);
        // AddingFlags:
        newTask.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newTask.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Returning:
        return newTask;
    }

    // Fast StartActivity:
    public static void runActivity(Context context, Class<?> cls) {
        // Initializing:
        Intent activity = new Intent(context, cls);
        // Running:
        context.startActivity(activity);
    }

    // DebuggingMethods:
    public static void debug(String tag, String field, String debug) {
        Log.d(tag, field + "-------------------------------------------");
        Log.d(tag, field + debug);
    }

    // Methods(ListOfChats):
    public static String fromChatsToJson(List<Chat> chat) {
        return new Gson().toJson(chat);
    }

    public static List<Chat> fromJsonToChats(String json) {
        Type type = new TypeToken<List<Chat>>() {

        }.getType();
        return new Gson().fromJson(json, type);
    }

    // Methods(OneChat):
    public static String fromChatToJson(Chat chat) {
        return new Gson().toJson(chat);
    }

    public static Chat fromJsonToChat(String json) {
        return new Gson().fromJson(json, Chat.class);
    }
}
