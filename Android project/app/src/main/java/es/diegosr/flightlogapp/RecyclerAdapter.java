package es.diegosr.flightlogapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerAdapter extends CursorRecyclerAdapter implements View.OnClickListener, View.OnLongClickListener {
    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;
    private String recyclerList;
    private BDAdapter bdAdapter;
    private RecyclerHolder recyclerHolder;
    private View cardView;

    public RecyclerAdapter(Cursor c, String recyclerList, Context context) {
        super(c);
        this.recyclerList = recyclerList;
        bdAdapter = new BDAdapter(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(recyclerList.equals("flights")) cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_flights, parent, false);
        else if(recyclerList.equals("bookings")) cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bookings, parent, false);
        recyclerHolder = new RecyclerHolder(cardView, recyclerList);
        cardView.setOnClickListener(this);
        cardView.setOnLongClickListener(this);
        return recyclerHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {
        if(recyclerList.equals("flights")) {
            ((RecyclerHolder) holder).bind(bdAdapter.cursorToFlight(cursor), cursor.getPosition());
        }
        else if(recyclerList.equals("bookings")) {
            ((RecyclerHolder) holder).bind(bdAdapter.cursorToBooking(cursor), cursor.getPosition());
        }
    }
    public int getItemCount() {
        return cursor.getCount();
    }

    public void MyClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    @Override
    public void onClick(View view) {
        if(listener != null) {
            listener.onClick(view);
        }
    }

    public void MyLongClick(View.OnLongClickListener listener) {
        this.longListener = listener;
    }
    @Override
    public boolean onLongClick(View v) {
        if(longListener != null) {
            longListener.onLongClick(v);
            return true;
        }
        return false;
    }
}
