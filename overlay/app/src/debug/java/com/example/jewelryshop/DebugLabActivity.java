package com.example.jewelryshop;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.*;
import com.example.jewelryshop.domain.ShopRules;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;
public class DebugLabActivity extends Activity {
    private static Activity retained; // deliberate debug-only leak
    private LinearLayout panel;
    private TextView output;
    private FrameLayout preview;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll=new ScrollView(this);
        panel=new LinearLayout(this); panel.setOrientation(1); panel.setPadding(24,24,24,24); scroll.addView(panel); setContentView(scroll);
        output=new TextView(this); output.setText("JewelryShop • RGR3 Debug Lab"); panel.addView(output);
        button("Log d/i/w/e", () -> { ShopLog.d("JS_LAB","DEBUG"); ShopLog.i("JS_LAB","INFO"); ShopLog.w("JS_LAB","WARN"); ShopLog.e("JS_LAB","ERROR",null); });
        button("Расчёт рассрочки: до / после", () -> output.setText("До: "+buggyMonthly(12990,12)+"; после: "+ShopRules.monthlyPayment(12990,12)));
        button("NullPointerException", () -> demo(() -> Objects.requireNonNull(null).toString()));
        button("IndexOutOfBoundsException", () -> demo(() -> Arrays.asList(1,2).get(3)));
        button("NetworkOnMainThreadException", () -> demo(() -> {
            StrictMode.ThreadPolicy old=StrictMode.getThreadPolicy();
            try { StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectNetwork().penaltyDeathOnNetwork().build());
                android.os.StrictMode.noteSlowCall("Network on main thread demo");
                try (java.net.Socket socket = new java.net.Socket()) { socket.connect(new java.net.InetSocketAddress("example.com", 80), 1500); }
            } catch (java.io.IOException e) { ShopLog.e("JS_LAB","DNS unavailable",e); }
            finally { StrictMode.setThreadPolicy(old); }
        }));
        button("Исправленные исключения", () -> { String s=null; output.setText((s==null?"Нет данных":s)+"; безопасный индекс; сеть через OfferActivity"); });
        preview=new FrameLayout(this); panel.addView(preview,new LinearLayout.LayoutParams(-1,120));
        button("Layout defect", () -> layout(true)); button("Layout fixed", () -> layout(false)); layout(false);
        button("Room + HTTP", () -> startActivity(new Intent(this,OfferActivity.class)));
        button("Создать утечку Activity", () -> { retained=this; finish(); });
        button("Исправить утечку", () -> { retained=null; output.setText("Ссылка очищена"); });
        button("StrictMode: запись на UI", this::writeSample);
        button("StrictMode: запись в фоне", () -> { Thread thread=new Thread(this::writeSample); thread.start(); });
    }
    private int buggyMonthly(int amount,int months) { int result=amount/months; return result; }
    private void demo(Runnable action) { try { action.run(); } catch (RuntimeException e) { ShopLog.e("JS_LAB",e.getClass().getSimpleName(),e); output.setText(e.toString()); } }
    private void button(String title,Runnable action) { Button b=new Button(this); b.setText(title); b.setOnClickListener(v->action.run()); panel.addView(b); }
    private void layout(boolean defect) { preview.removeAllViews(); TextView a=new TextView(this),b=new TextView(this); a.setText("Цена: 12 990 ₸"); b.setText("Рассрочка: 1 083 ₸"); a.setTag("price"); b.setTag("installment"); preview.addView(a,new FrameLayout.LayoutParams(-1,-2)); FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(-1,-2); p.topMargin=defect?0:52; preview.addView(b,p); }
    private void writeSample() { try (FileOutputStream out=new FileOutputStream(new File(getCacheDir(),"rgr3.txt"))) { out.write(65); } catch (java.io.IOException e) { ShopLog.e("JS_LAB","Disk write",e); } }
}
