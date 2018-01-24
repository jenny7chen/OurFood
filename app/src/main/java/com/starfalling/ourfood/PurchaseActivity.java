package com.starfalling.ourfood;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.starfalling.ourfood.model.Purchase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class PurchaseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.material_activity);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        queryPurchases();
    }

    private void queryPurchases() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.where(Purchase.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Purchase>>() {
                @Override
                public void onChange(@NonNull RealmResults<Purchase> purchaseResults) {
                    ArrayList<Purchase> purchases = new ArrayList<>();

                    purchases.addAll(purchaseResults);
                    PurchaseListAdapter adapter = new PurchaseListAdapter(purchases);
                    if (recyclerView != null && !isDestroyed()) {
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "發生錯誤:error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.Holder> {
        private ArrayList<Purchase> purchases;

        public PurchaseListAdapter(ArrayList<Purchase> purchases) {
            this.purchases = purchases;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_item_view, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Purchase purchase = purchases.get(position);
            if (purchase != null) {
                holder.purchase = purchase;
                holder.nameView.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date(purchase.getTime())));
                String text = "花費了" + purchase.getSpent() + "元";
                holder.priceView.setText(text);
            }
        }

        @Override
        public int getItemCount() {
            return purchases == null ? 0 : purchases.size();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            TextView nameView;
            TextView priceView;
            Purchase purchase;

            public Holder(View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.name_view);
                priceView = itemView.findViewById(R.id.price_view);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View view) {
                new MaterialDialog.Builder(PurchaseActivity.this)
                        .items("刪除")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                if (position == 0) {
                                    PurchaseUtils.showDeletePurchaseDialog(PurchaseActivity.this, purchase, new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            queryPurchases();
                                        }
                                    });
                                }
                            }
                        })
                        .build().show();
                return false;
            }


        }
    }

}
