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
import com.starfalling.ourfood.model.Material;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MaterialActivity extends AppCompatActivity {
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
        queryMaterials();
    }

    private void queryMaterials() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.where(Material.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Material>>() {
                @Override
                public void onChange(@NonNull RealmResults<Material> materialResults) {
                    ArrayList<Material> materials = new ArrayList<>();

                    materials.addAll(materialResults);
                    MaterialListAdapter adapter = new MaterialListAdapter(materials);
                    if (recyclerView != null && !isDestroyed()) {
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "發生錯誤:error = " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class MaterialListAdapter extends RecyclerView.Adapter<MaterialListAdapter.Holder> {
        private ArrayList<Material> materials;

        public MaterialListAdapter(ArrayList<Material> materials) {
            this.materials = materials;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_list_item_view, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Material material = materials.get(position);
            if (material != null) {
                holder.material = material;
                holder.nameView.setText(material.getName());
                String text = "一" + material.getPriceUnit() + material.getPrice() + "元";
                holder.priceView.setText(text);
            }
        }

        @Override
        public int getItemCount() {
            return materials == null ? 0 : materials.size();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
            TextView nameView;
            TextView priceView;
            Material material;

            public Holder(View itemView) {
                super(itemView);
                nameView = itemView.findViewById(R.id.name_view);
                priceView = itemView.findViewById(R.id.price_view);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View view) {
                new MaterialDialog.Builder(MaterialActivity.this)
                        .items("編輯", "刪除")
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                if (position == 0) {
                                    MaterialUtils.showAddMaterialDialog(MaterialActivity.this, material, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            queryMaterials();
                                        }
                                    });
                                } else {
                                    MaterialUtils.showDeleteDialog(MaterialActivity.this, material, new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            queryMaterials();
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
