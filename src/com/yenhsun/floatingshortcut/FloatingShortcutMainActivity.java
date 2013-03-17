
package com.yenhsun.floatingshortcut;

import com.yenhsun.floatingshortcut.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.view.Menu;

public class FloatingShortcutMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.startService(new Intent(this, FloatingShortcutService.class));
        this.finish();
    }

}
