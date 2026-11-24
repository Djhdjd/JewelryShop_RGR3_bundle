#!/usr/bin/env python3
"""Apply the RGR3 overlay to a clean checkout of Djhdjd/JewelryShop."""
from pathlib import Path
import sys, shutil, subprocess, re
bundle=Path(__file__).resolve().parent
if len(sys.argv)!=2: raise SystemExit('Usage: python3 apply_rgr3.py /path/to/JewelryShop')
project=Path(sys.argv[1]).expanduser().resolve()
if not (project/'app/src/main/java/com/example/jewelryshop/Catalog.java').is_file(): raise SystemExit('Not the original JewelryShop project')
if (project/'app/src/main/java/com/example/jewelryshop/domain/ShopRules.java').exists(): raise SystemExit('RGR3 already applied; use a fresh checkout')
if (project/".git").exists():
    status=subprocess.run(["git","-C",str(project),"status","--porcelain"],capture_output=True,text=True,check=True).stdout
    if status.strip(): raise SystemExit("Working tree is not clean; commit/stash existing changes first.")
# Do all context checks before writing anything.
changes={}
base='app/src/main/java/com/example/jewelryshop/'
def get(path): return changes.get(path,(project/path).read_text(encoding='utf-8'))
def replace(path,old,new):
 s=get(path)
 if s.count(old)!=1: raise SystemExit(f'Unexpected source in {path}: {old[:100]!r}. No changes applied.')
 changes[path]=s.replace(old,new,1)
def import_rules(name): replace(base+name+'.java','package com.example.jewelryshop;','package com.example.jewelryshop;\n\nimport com.example.jewelryshop.domain.ShopRules;')
for name in ['Catalog','CheckoutActivity','InstallmentActivity','OrderStore','MainActivity','ProductActivity','CartActivity']: import_rules(name)
p=base+'Catalog.java'
replace(p,'return c.getSharedPreferences("cart", Context.MODE_PRIVATE).getInt("item_" + index, 0);','return ShopRules.quantity(c.getSharedPreferences("cart", Context.MODE_PRIVATE).getInt("item_" + index, 0));')
replace(p,'Math.max(0, count)).apply();','ShopRules.quantity(count)).apply();\n        ShopLog.d("JS_CART", "Cart quantity changed");')
replace(p,'int n = 0;\n        for (int i = 0; i < NAMES.length; i++) n += count(c, i) * PRICES[i];\n        return n;','int[] counts = new int[NAMES.length];\n        for (int i = 0; i < counts.length; i++) counts[i] = count(c, i);\n        return ShopRules.cartTotal(PRICES, counts);')
p=base+'CartActivity.java';replace(p,'Catalog.PRICES[i] * qty','ShopRules.lineTotal(Catalog.PRICES[i], qty)')
p=base+'OrderStore.java';replace(p,'Math.max(0, Math.min(STAGES.length - 1, order.optInt("stage", 0)))','ShopRules.stage(order.optInt("stage", 0))');replace(p,'total += Catalog.PRICES[i] * quantity;','total += ShopRules.lineTotal(Catalog.PRICES[i], quantity);');replace(p,'if (count == 0) throw new JSONException("Корзина пуста");','if (count == 0) throw new JSONException("Корзина пуста");\n        if (!ShopRules.validReceiver(receiver) || !ShopRules.validAddress(address)) throw new JSONException("Invalid order data");');replace(p,'return order;\n    }\n\n    public static synchronized boolean advanceManually','ShopLog.i("JS_ORDER", "Order persisted");\n        return order;\n    }\n\n    public static synchronized boolean advanceManually')
p=base+'InstallmentActivity.java';replace(p,'int base = amount / months, extra = amount % months;','int base = ShopRules.basePayment(amount, months), extra = ShopRules.largerPayments(amount, months);');replace(p,'(amount + months - 1) / months','ShopRules.monthlyPayment(amount, months)')
p=base+'CheckoutActivity.java';replace(p,'name.length() < 2','!ShopRules.validReceiver(name)');replace(p,'location.length() < 5','!ShopRules.validAddress(location)')
# Move synchronous persistence off the UI thread; retain original validation and navigation.
s=get(p);start=s.index('        confirm.setEnabled(false);');end=s.index('\n    }\n}',start)
changes[p]=s[:start]+'''        confirm.setEnabled(false);
        final String selectedPayment = payment;
        final android.content.Context app = getApplicationContext();
        OperationTracker.begin();
        new Thread(() -> {
            JSONObject result = null;
            try { result = OrderStore.create(app, name, location, selectedPayment); }
            catch (JSONException | RuntimeException error) { ShopLog.e("JS_ORDER", "Order save failed", error); }
            final JSONObject order = result;
            runOnUiThread(() -> {
                try {
                    if (isDestroyed() || isFinishing()) return;
                    if (order == null) {
                        Toast.makeText(this, "Не удалось сохранить заказ", Toast.LENGTH_LONG).show();
                        confirm.setEnabled(true);
                    } else {
                        Intent intent = new Intent(this, TrackingActivity.class);
                        intent.putExtra("order_id", order.optString("id"));
                        startActivity(intent);
                        finish();
                    }
                } finally { OperationTracker.end(); }
            });
        }, "jewelry-order-io").start();''' +s[end:]
p=base+'MainActivity.java';replace(p,'Button add = actionBtn("В корзину", R.drawable.cart_blue_bg);','Button add = actionBtn("В корзину", R.drawable.cart_blue_bg);\n        add.setTag("add_" + index);');replace(p,'(Catalog.PRICES[index] + 11) / 12','ShopRules.monthlyPayment(Catalog.PRICES[index], 12)');replace(p,'NavUtil.setup(this, "store");','''NavUtil.setup(this, "store");
        if (BuildConfig.DEBUG) {
            Button lab = new Button(this);
            lab.setText("Лаборатория отладки · РГР №3");
            ((LinearLayout) list.getParent()).addView(lab, 0);
            lab.setOnClickListener(v -> startActivity(new Intent().setClassName(this, "com.example.jewelryshop.DebugLabActivity")));
        }''')
replace(p,'super.onResume();\n        renderList();','super.onResume(); // catalog is static; avoid rebuilding all cards twice on startup')
# Source in repository may contain literal line breaks inside Java strings; normalize these two known labels.
changes[p]=get(p).replace('" с\nбонусами"','" с\\nбонусами"').replace('"В рассрочку\n"','"В рассрочку\\n"')
p=base+'ProductActivity.java';replace(p,'(Catalog.PRICES[product] + 11) / 12','ShopRules.monthlyPayment(Catalog.PRICES[product], 12)');replace(p,'NavUtil.setup(this, "store");','''NavUtil.setup(this, "store");
        Button offer = new Button(this);
        offer.setText("Демо-наличие · API и Room");
        // Keep original product layout; add the visible demo entry above the scroll area.
        ((android.widget.TextView) findViewById(R.id.detailPrice)).setOnLongClickListener(v -> {
            openOffer(); return true;
        });
        android.view.ViewGroup root = (android.view.ViewGroup) ((android.view.ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        root.addView(offer, 0);
        offer.setOnClickListener(v -> openOffer());''');replace(p,'\n}\n','\n    private void openOffer() { startActivity(new Intent(this, OfferActivity.class).putExtra("product_index", product)); }\n}\n')
p='app/src/main/AndroidManifest.xml';replace(p,'    <application android:allowBackup="true"','    <uses-permission android:name="android.permission.INTERNET"/>\n    <application android:name=".ShopApplication" android:allowBackup="true"');replace(p,'        <activity android:name=".ProductActivity"','        <activity android:name=".OfferActivity" android:exported="false"/>\n        <activity android:name=".ProductActivity"')
p='app/src/main/res/layout/activity_main.xml';replace(p,'    <ScrollView\n','    <ScrollView\n        android:id="@+id/catalogScroll"\n')
# Preserve original ignores and add only missing project-generated directories.
ignore = project/'.gitignore'
old_ignore = ignore.read_text(encoding='utf-8') if ignore.exists() else ''
extra = ['.gradle/', '.idea/', 'local.properties', '**/build/', '*.iml', 'evidence/']
changes['.gitignore'] = old_ignore.rstrip() + '\n' + '\n'.join(x for x in extra if x not in old_ignore.splitlines()) + '\n'
# avoid overwriting local changes: explicit file replacement list, git remains intact.
for path,s in changes.items(): (project/path).write_text(s,encoding='utf-8')
for src in (bundle/'overlay').rglob('*'):
 if src.is_file():
  dest=project/src.relative_to(bundle/'overlay');dest.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(src,dest)
(project/'gradlew').chmod(0o755)
print(f'JewelryShop RGR3 applied: {len(changes)} adapted original files + overlay. Open Android Studio and run Gradle checks.')
