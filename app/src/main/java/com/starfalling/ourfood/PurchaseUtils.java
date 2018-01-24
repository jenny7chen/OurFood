package com.starfalling.ourfood;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.starfalling.ourfood.model.Material;
import com.starfalling.ourfood.model.MaterialId;
import com.starfalling.ourfood.model.Purchase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class PurchaseUtils {
    public static void savePurchase(final Context context, final Purchase purchase) {
        final Realm realm = Realm.getDefaultInstance();
        if (purchase.getId() == null) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    Purchase purchaseNew = new Purchase();
                    purchaseNew.setNote(purchase.getNote());
                    purchaseNew.setSpent(purchase.getSpent());
                    purchaseNew.setId(String.valueOf(System.currentTimeMillis()));
                    purchaseNew.setTime(purchase.getTime());
                    purchaseNew.setMaterialIds(purchase.getMaterialIds());
                    bgRealm.copyToRealmOrUpdate(purchaseNew);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "新增一筆消費於" + new Date(purchase.getTime()) + "成功!", Toast.LENGTH_SHORT).show();
                    realm.close();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toast.makeText(context, "新增消費時發生錯誤,請再試一次: error = " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    realm.close();
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try {
                        final Purchase result = realm.where(Purchase.class).equalTo("id", purchase.getId()).findFirst();
                        if (result != null) {
                            result.setNote(purchase.getNote());
                            result.setTime(purchase.getTime());
                            result.setSpent(purchase.getSpent());
                            result.setMaterialIds(purchase.getMaterialIds());
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "編輯此筆消費時發生錯誤,請再試一次: error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    realm.close();
                }
            });
        }
    }

    public static void deletePurchase(final Context context, final Purchase purchase) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    final Purchase result = realm.where(Purchase.class).equalTo("id", purchase.getId()).findFirst();
                    result.deleteFromRealm();
                } catch (Exception e) {
                    Toast.makeText(context, "刪除消費時發生錯誤,請再試一次: error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                realm.close();
            }
        });
    }

    public static void showAddPurchaseDateDialog(final Context context) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                cal.set(Calendar.MONTH, view.getMonth());
                cal.set(Calendar.YEAR, view.getYear());

                Purchase purchase = new Purchase();
                purchase.setTime(cal.getTimeInMillis());
                showAddPurchaseMaterialDialog(context, purchase);
            }
        }, year, month, day).show();

    }

    public static void showAddPurchaseMaterialDialog(final Context context, final Purchase purchase) {
        Realm realm = Realm.getDefaultInstance();
        realm.where(Material.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Material>>() {
            @Override
            public void onChange(@NonNull RealmResults<Material> materialResults) {
                final ArrayList<Material> materials = new ArrayList<>();
                materials.addAll(materialResults);

                if(materials.size()==0){
                    purchase.setMaterialIds(new RealmList<MaterialId>());
                    showAddPurchaseDialog(context, purchase);
                    return;
                }
                ArrayList<String> materialNames = new ArrayList<>();
                for(Material material:materials){
                    materialNames.add(material.getName());
                }
                final RealmList<MaterialId> choices = new RealmList<>();
                new MaterialDialog.Builder(context)
                        .items(materialNames)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                choices.clear();
                                for(int i : which) {
                                    MaterialId materialId = new MaterialId();
                                    materialId.setId(materials.get(i).getId());
                                    choices.add(materialId);
                                }
                                return true;
                            }
                        })
                        .positiveText("確定")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                purchase.setMaterialIds(choices);
                                showAddPurchaseDialog(context, purchase);
                            }
                        })
                        .build().show();
            }
        });

    }

    public static void showAddPurchaseDialog(final Context context, final Purchase purchase) {
        MultiInputMaterialDialogBuilder.InputValidator nonEmptyInputValidator = new MultiInputMaterialDialogBuilder.InputValidator() {
            @Override
            public CharSequence validate(CharSequence input) {
                return TextUtils.isEmpty(input) ? context.getString(android.R.string.no) : null;
            }
        };
        new MultiInputMaterialDialogBuilder(context)
                .addInput(InputType.TYPE_CLASS_TEXT, purchase.getNote() == null ? "" : purchase.getNote(), "消費註記", nonEmptyInputValidator)
                .addInput(InputType.TYPE_CLASS_NUMBER, purchase.getSpent() == 0 ? "" : String.valueOf(purchase.getSpent()), "總花費", nonEmptyInputValidator)
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        if (allInputsValidated) {
                            String note = inputs.get(0).toString();
                            long money = Long.parseLong(inputs.get(1).toString());
                            Purchase purchaseNew = new Purchase();
                            purchaseNew.setId(purchase.getId());
                            purchaseNew.setNote(note);
                            purchaseNew.setSpent(money);
                            purchaseNew.setTime(purchase.getTime());
                            PurchaseUtils.savePurchase(context, purchaseNew);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "請輸入正確的資料", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .title(purchase.getId() == null ? "新增消費" : "編輯消費")
                .positiveText("完成")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build()
                .show();

    }

    public static void showDeletePurchaseDialog(Context context, Purchase material) {
        showDeletePurchaseDialog(context, material, null);
    }

    public static void showDeletePurchaseDialog(final Context context, final Purchase purchase, final MaterialDialog.SingleButtonCallback callback) {
        if (purchase == null) {
            return;
        }
        new MaterialDialog.Builder(context)
                .title("是否確定刪除材料" + purchase.getNote() + "?")
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText("確定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deletePurchase(context, purchase);
                        if (callback != null) {
                            callback.onClick(dialog, which);
                        }
                    }
                })
                .build().show();
    }
}
