package com.androidavanzado.minitwitter.userinterface.tweets;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidavanzado.minitwitter.R;
import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;
import com.androidavanzado.minitwitter.data.TweetViewModel;
import com.androidavanzado.minitwitter.retrofit.response.Like;
import com.androidavanzado.minitwitter.retrofit.response.Tweet;
import com.bumptech.glide.Glide;

import java.util.List;


public class MyTweetRecyclerViewAdapter extends RecyclerView.Adapter<MyTweetRecyclerViewAdapter.ViewHolder> {

    private Context ctx;
    private List<Tweet> mValues;
    String username;
    TweetViewModel tweetViewModel;

    public MyTweetRecyclerViewAdapter(Context contexto, List<Tweet> items) {
        mValues = items;
        ctx = contexto;
        username = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
        tweetViewModel = ViewModelProviders.of((FragmentActivity) ctx).get(TweetViewModel.class);
    }

    // Método que carga la interfaz
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Método que se encarga de dibujar cada elemento
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues != null){
            holder.mItem = mValues.get(position);

            holder.tvUsername.setText("@" + holder.mItem.getUser().getUsername());
            holder.tvMessage.setText(holder.mItem.getMensaje());
            holder.tvLikesCount.setText(String.valueOf(holder.mItem.getLikes().size()));

            // Función que carga la foto de prefil con la ayuda de la librería Glide
            String photo = holder.mItem.getUser().getPhotoUrl();
            if(!photo.equals("")){
                Glide.with(ctx)
                        .load("https://www.minitwitter.com/apiv1/uploads/photos/" + photo)
                        .into(holder.ivAvatar);
            }

            Glide.with(ctx)
                    .load(R.drawable.ic_like)
                    .into(holder.ivLike);
            holder.tvLikesCount.setTextColor(ctx.getResources().getColor(android.R.color.black));
            holder.tvLikesCount.setTypeface(null, Typeface.NORMAL);

            // Esto hace que el menú desplegable solo aparezca si el tweet es de la persona logeada
            holder.ivShowMenu.setVisibility(View.GONE);
            if(holder.mItem.getUser().getUsername().equals(username)){
                holder.ivShowMenu.setVisibility(View.VISIBLE);
            }

            // Gestión del menu desplegable
            holder.ivShowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetViewModel.openDialogTweetMenu(ctx, holder.mItem.getId());
                }
            });

            // Función que identifica si nos hemos dado like en nuestro tweet, y cambia el color
            // del ImageView
            holder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetViewModel.likeTweet(holder.mItem.getId());
                }
            });

            for(Like like : holder.mItem.getLikes()){
                if(like.getUsername().equals(username)){
                    Glide.with(ctx)
                            .load(R.drawable.ic_like_pink)
                            .into(holder.ivLike);
                    holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.pink));
                    holder.tvLikesCount.setTypeface(null, Typeface.BOLD);
                    break;
                }
            }
        }

    }

    // Método que refresca los datos
    public void setData(List<Tweet> tweetList){
        this.mValues = tweetList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mValues != null)
            return mValues.size();
        else return 0;
    }

    // Clase que se encarga de coger todos los elementos de la interfaz
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Esta primera variable hacer referencia al Constraint Layout
        public final View mView;
        public final ImageView ivAvatar;
        public final ImageView ivLike;
        public final ImageView ivShowMenu;
        public final TextView tvUsername;
        public final TextView tvMessage;
        public final TextView tvLikesCount;
        public Tweet mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivAvatar = view.findViewById(R.id.imageViewAvatar);
            ivLike = view.findViewById(R.id.imageViewLike);
            ivShowMenu = view.findViewById(R.id.imageViewShowMenu);
            tvUsername = view.findViewById(R.id.textViewUsername);
            tvMessage = view.findViewById(R.id.textViewMessage);
            tvLikesCount = view.findViewById(R.id.textViewLikes);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvUsername.getText() + "'";
        }
    }
}
