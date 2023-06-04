package com.example.gobang.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import androidx.annotation.UiThread;
import com.example.gobang.MainActivity;

public class NestedDialog {
    private AlertDialog alertDialog;
    protected final Context self;
    public NestedDialog(Context self){
        this.self=self;
    }
    @UiThread
    protected void createDialog(AlertDialog.Builder dialogBuilder){
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    @UiThread
    public void alert(String title,String msg,String yes, Runnable yesCall){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(yes, (dialog,which)->{
            dialog.cancel();
            yesCall.run();
        });
        createDialog(dialogBuilder);
    }
    @UiThread
    public void alert(String title,String msg,String yes, Runnable yesCall,String no, Runnable noCall){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton(yes, (dialog,which)->{
            dialog.cancel();
            yesCall.run();
        });
        dialogBuilder.setNegativeButton(no, (dialog,which)->{
            dialog.cancel();
            noCall.run();
        });
        createDialog(dialogBuilder);
    }
    public interface OnInputListener{
        public void run(String input);
    }
    @UiThread
    public void input(String title,String msg,int inputType,String yes, OnInputListener yesCall){
        EditText editView = new EditText(self);
        editView.setInputType(inputType);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setView(editView);
        dialogBuilder.setPositiveButton(yes,(dialog,which)->{
            dialog.cancel();
            yesCall.run(editView.getText().toString());
        });
        createDialog(dialogBuilder);
    }
    @UiThread
    public void input(String title,String msg,int inputType,String yes, OnInputListener yesCall,String no, OnInputListener noCall){
        EditText editView = new EditText(self);
        editView.setInputType(inputType);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(self);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setView(editView);
        dialogBuilder.setPositiveButton(yes,(dialog,which)->{
            dialog.cancel();
            yesCall.run(editView.getText().toString());
        });
        dialogBuilder.setNegativeButton(no,(dialog,which)->{
            dialog.cancel();
            noCall.run(editView.getText().toString());
        });
        createDialog(dialogBuilder);
    }
    public void cancel(){
        alertDialog.cancel();
    }
    public void exit(){
        Intent attractionIntent = new Intent(self, MainActivity.class);
        self.startActivity(attractionIntent);
    }
}
