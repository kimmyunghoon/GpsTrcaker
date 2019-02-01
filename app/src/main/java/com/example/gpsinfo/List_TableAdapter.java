package com.example.gpsinfo;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class List_TableAdapter extends BaseAdapter {

    private ArrayList<List_Table> list_tables;
    private Context context;
    private ArrayList<Listitem> mItems;
    private LayoutInflater inflater;
 /*   Holder holder;*/
    private int mLayout;

    public List_TableAdapter(ArrayList<List_Table> list_tables,Context context) {
        this.list_tables = list_tables;
        this.context = context;

    }


    @Override
    public int getCount() {
        return this.list_tables.size();
    }




    public Object getItem(int position) {
        return this.list_tables.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       /*  if (convertView == null) {

           convertView = inflater.inflate(mLayout, parent, false);

            holder = new Holder();
            holder.imgV = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvDaytime = (TextView) convertView.findViewById(R.id.tv_contents);


            convertView.setTag(holder);
        }
        holder.tvTitle.setText(mItems.get(position).getName());
        holder.tvDaytime.setText(mItems.get(position).getContents());*/

         /*   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);*//*
            convertView = new LinearLayout(context);
            ((LinearLayout) convertView).setOrientation(LinearLayout.HORIZONTAL);

            TextView txDaytime = new TextView(context);
            TextView txTitle = new TextView(context);
            holder.imgV = (ImageView) convertView.findViewById(R.id.iv_img);
            ((LinearLayout) convertView).addView(txTitle);
            ((LinearLayout) convertView).addView(txDaytime);
            holder.tvTitle = txTitle;
            holder.tvDaytime = txDaytime;
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        List_Table list_table = (List_Table) getItem(position);

        holder.tvTitle.setText(list_table.getTitle());
        holder.tvDaytime.setText(list_table.getDaytime());*/

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_custom, parent, false);

            }

            // 'listview_custom'에 정의된 위젯에 대한 참조 획득
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_contents = (TextView) convertView.findViewById(R.id.tv_contents);

            //  각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용


            List_Table list_table = (List_Table) getItem(position);
            //  각 위젯에 세팅된 아이템을 뿌려준다

            iv_img.setImageDrawable(list_table.getIcon());
            tv_name.setText(list_table.getTitle());

            tv_contents.setText(list_table.getDaytime());


        return convertView;
    }

    public void addItem(Drawable img) {

             Listitem listitem = new Listitem();

        // MyItem에 아이템을 setting한다.
        listitem.setIcon(img);
        // mItems에 MyItem을 추가한다.
        mItems.add(listitem);
        }


    }

/*

class Holder{

    public ImageView imgV;
    public TextView tvTitle;
    public TextView tvDaytime;
}
*/
