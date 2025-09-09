package es.diegosr.flightlogapp;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerAdapter extends RecyclerView.Adapter {
    Cursor cursor;

    public CursorRecyclerAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(cursor == null) throw new IllegalStateException("ERROR, cursor vacío");
        if(!cursor.moveToPosition(position)) throw new IllegalStateException("ERROR, no se puede encontrar la posicion : " + position);

        onBindViewHolder(holder, cursor);
    }
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor);
    @Override
    public int getItemCount() {
        if(cursor != null) return cursor.getCount();
        else return 0;
    }
}
