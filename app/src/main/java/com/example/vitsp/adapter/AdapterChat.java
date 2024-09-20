//package com.example.vitsp.adapter;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.models.ModelChat;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.installations.Utils;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {
//
//
//
//    private static final String TAG = "ADAPTER_CHAT_TAG";
//    private FirebaseAuth firebaseAuth;
//
//
//    private Context context;
//    private ArrayList<ModelChat> chatArrayList;
//
//    public static final int MSG_TYPE_LEFT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;
//
//
//    public FirebaseUser firebaseUser;
//
//    public AdapterChat(Context context, ArrayList<ModelChat> chatArrayList) {
//        this.context = context;
//        this.chatArrayList = chatArrayList;
//
//        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
//    }
//
//    @NonNull
//    @Override
//    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        if (viewType == MSG_TYPE_RIGHT){
//            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
//            return new HolderChat(view);
//        }
//        else{
//            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
//            return new HolderChat(view);
//        }
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
//
//        ModelChat modelChat = chatArrayList.get(position);
//        String message = modelChat.getMessage();
//        String timeStamp = modelChat.getTimestamp();
//        String messageType = modelChat.getMessageType();
//        String formattedDate = Utils.formatTimestampDateTime(timeStamp);
//
//        if(messageType.equals(Utils.MESSAGE_TYPE_TEXT)){
//            holder.messageTv.setVisibility(View.VISIBLE);
//            holder.imageiv.setVisibility(View.GONE);
//            holder.messageTv.setText(message);
//        }
//        else{
//            holder.messageTv.setVisibility(View.GONE);
//            holder.imageiv.setVisibility(View.VISIBLE);
//
//            try{
//                Glide.with(context)
//                        .load(message)
//                        .error(R.drawable.ic_image_broken_gray)
//                        .into(holder.imageiv);
//            }
//            catch (Exception e){
//                Log.e(TAG,"onBindViewHolder: ",e);
//
//            }
//        }
//        holder.timeTv.setText(formattedDate);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatArrayList.size();
//    }
//
//    public int  getItemViewType(int position){
//        if (chatArrayList.get(position).getfromUid().equals(firebaseUser.getUid())){
//            return MSG_TYPE_RIGHT;
//        }else {
//            return MSG_TYPE_LEFT;
//        }
//    }
//
//
//    class HolderChat extends RecyclerView.ViewHolder {
//
//        TextView messageTv , timeTv;
//        ImageView imageiv;
//        public HolderChat(View itemView) {
//            super(itemView);
//            messageTv = itemView.findViewById(R.id.messageTV);
//            imageiv =itemView.findViewById(R.id.imageiv);
//            timeTv = itemView.findViewById(R.id.timeTV);
//        }
//
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



















































//
//package com.example.vitsp.adapter;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.models.ModelChat;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//
//public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {
//
//    private static final String TAG = "ADAPTER_CHAT_TAG";
//
//    private Context context;
//    private ArrayList<ModelChat> chatArrayList;
//
//    private FirebaseUser firebaseUser;
//
//    public static final int MSG_TYPE_LEFT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;
//
//    public AdapterChat(Context context, ArrayList<ModelChat> chatArrayList) {
//        this.context = context;
//        this.chatArrayList = chatArrayList;
//        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    }
//
//    @NonNull
//    @Override
//    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == MSG_TYPE_RIGHT) {
//            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
//            return new HolderChat(view);
//        } else {
//            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
//            return new HolderChat(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
//        ModelChat modelChat = chatArrayList.get(position);
//        String message = modelChat.getMessage();
//        String messageType = modelChat.getMessageType();
//        String formattedDate = Utils.formatTimestampDateTime(modelChat.getTimestamp());
//
//        if (messageType.equals(Utils.MESSAGE_TYPE_TEXT)) {
//            holder.messageTv.setVisibility(View.VISIBLE);
//            holder.imageiv.setVisibility(View.GONE);
//            holder.messageTv.setText(message);
//        } else {
//            holder.messageTv.setVisibility(View.GONE);
//            holder.imageiv.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(message)
//                    .error(R.drawable.ic_image_broken_gray)
//                    .into(holder.imageiv);
//        }
//        holder.timeTv.setText(formattedDate);
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatArrayList.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (chatArrayList.get(position).getFromUid().equals(firebaseUser.getUid())) {
//            return MSG_TYPE_RIGHT;
//        } else {
//            return MSG_TYPE_LEFT;
//        }
//    }
//
//    class HolderChat extends RecyclerView.ViewHolder {
//
//        TextView messageTv, timeTv;
//        ImageView imageiv;
//
//        public HolderChat(View itemView) {
//            super(itemView);
//            messageTv = itemView.findViewById(R.id.messageTV);
//            imageiv = itemView.findViewById(R.id.imageiv);
//            timeTv = itemView.findViewById(R.id.timeTV);
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//





















































//
//
//package com.example.vitsp.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.models.ModelChat;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//
//public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {
//
//    private static final String TAG = "ADAPTER_CHAT_TAG";
//
//    private Context context;
//    private ArrayList<ModelChat> chatArrayList;
//    private FirebaseUser firebaseUser;
//
//    public static final int MSG_TYPE_LEFT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;
//
//    public AdapterChat(Context context, ArrayList<ModelChat> chatArrayList) {
//        this.context = context;
//        this.chatArrayList = chatArrayList;
//        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    }
//
//    @NonNull
//    @Override
//    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == MSG_TYPE_RIGHT) {
//            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
//        } else {
//            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
//        }
//        return new HolderChat(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
//        ModelChat modelChat = chatArrayList.get(position);
//        String message = modelChat.getMessage();
//        String messageType = modelChat.getMessageType();
//        String formattedDate = Utils.formatTimestampDateTime(modelChat.getTimestamp());
//
//        if (messageType.equals(Utils.MESSAGE_TYPE_TEXT)) {
//            holder.messageTv.setVisibility(View.VISIBLE);
//            holder.imageiv.setVisibility(View.GONE);
//            holder.messageTv.setText(message);
//        } else {
//            holder.messageTv.setVisibility(View.GONE);
//            holder.imageiv.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(message)
//                    .error(R.drawable.ic_image_broken_gray)
//                    .into(holder.imageiv);
//        }
//        holder.timeTv.setText(formattedDate);
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatArrayList.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return chatArrayList.get(position).getFromUid().equals(firebaseUser.getUid()) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
//    }
//
//    static class HolderChat extends RecyclerView.ViewHolder {
//
//        TextView messageTv, timeTv;
//        ImageView imageiv;
//
//        public HolderChat(View itemView) {
//            super(itemView);
//            messageTv = itemView.findViewById(R.id.messageTV);
//            imageiv = itemView.findViewById(R.id.imageiv);
//            timeTv = itemView.findViewById(R.id.timeTV);
//        }
//    }
//}
//





































































































//
//
//package com.example.vitsp.adapter;
//
//import static android.content.ContentValues.TAG;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.vitsp.R;
//import com.example.vitsp.Utils;
//import com.example.vitsp.models.ModelChat;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import java.util.ArrayList;
//
//public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {
//
//    private static final String TAG = "ADAPTER_CHAT_TAG";
//
//    private Context context;
//    private ArrayList<ModelChat> chatArrayList;
//
//    private FirebaseUser firebaseUser;
//
//    public static final int MSG_TYPE_LEFT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;
//
//    public AdapterChat(Context context, ArrayList<ModelChat> chatArrayList) {
//        this.context = context;
//        this.chatArrayList = chatArrayList;
//        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    }
//
//    @NonNull
//    @Override
//    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == MSG_TYPE_RIGHT) {
//            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
//        } else {
//            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
//        }
//        return new HolderChat(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
//        if (position < 0 || position >= chatArrayList.size()) {
//            Log.e(TAG, "onBindViewHolder: Invalid position: " + position);
//            return;
//        }
//
//        ModelChat modelChat = chatArrayList.get(position);
//        if (modelChat == null) {
//            Log.e(TAG, "onBindViewHolder: ModelChat is null at position: " + position);
//            return;
//        }
//
//        String message = modelChat.getMessage();
//        String messageType = modelChat.getMessageType();
//        String formattedDate = Utils.formatTimestampDateTime(modelChat.getTimestamp());
//
//        if (messageType != null && messageType.equals(Utils.MESSAGE_TYPE_TEXT)) {
//            holder.messageTv.setVisibility(View.VISIBLE);
//            holder.imageiv.setVisibility(View.GONE);
//            holder.messageTv.setText(message != null ? message : "No message");
//        } else {
//            holder.messageTv.setVisibility(View.GONE);
//            holder.imageiv.setVisibility(View.VISIBLE);
//            Glide.with(context)
//                    .load(message)
//                    .error(R.drawable.ic_image_broken_gray)
//                    .into(holder.imageiv);
//        }
//
//        holder.timeTv.setText(formattedDate != null ? formattedDate : "Unknown time");
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatArrayList != null ? chatArrayList.size() : 0;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (firebaseUser != null && chatArrayList.get(position) != null &&
//                chatArrayList.get(position).getFromUid() != null) {
//            return chatArrayList.get(position).getFromUid().equals(firebaseUser.getUid()) ?
//                    MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
//        } else {
//            Log.e(TAG, "getItemViewType: FirebaseUser or ModelChat is null");
//            return MSG_TYPE_LEFT;
//        }
//    }
//
//    class HolderChat extends RecyclerView.ViewHolder {
//
//        TextView messageTv, timeTv;
//        ImageView imageiv;
//
//        public HolderChat(View itemView) {
//            super(itemView);
//            messageTv = itemView.findViewById(R.id.messageTV);
//            imageiv = itemView.findViewById(R.id.imageiv);
//            timeTv = itemView.findViewById(R.id.timeTV);
//        }
//    }
//}
























package com.example.vitsp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vitsp.R;
import com.example.vitsp.Utils;
import com.example.vitsp.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {

    private static final String TAG = "ADAPTER_CHAT_TAG";

    private Context context;
    private ArrayList<ModelChat> chatArrayList;
    private FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public AdapterChat(Context context, ArrayList<ModelChat> chatArrayList) {
        this.context = context;
        this.chatArrayList = chatArrayList;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new HolderChat(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new HolderChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {
        ModelChat modelChat = chatArrayList.get(position);
        String message = modelChat.getMessage();
        String messageType = modelChat.getMessageType();
        long TimeStamp = modelChat.getTimestamp();

        String formattedDate = Utils.formatTimestampDateTime(TimeStamp);

        if (messageType.equals(Utils.MESSAGE_TYPE_TEXT)) {
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.imageiv.setVisibility(View.GONE);
            holder.messageTv.setText(message);

        }
        else {
            holder.messageTv.setVisibility(View.GONE);
            holder.imageiv.setVisibility(View.VISIBLE);


            try{
                Glide.with(context)
                        .load(message)
                        .placeholder(R.drawable.ic_image_gray)
                        .error(R.drawable.ic_image_broken_gray)
                        .into(holder.imageiv);

            }
            catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        }


        holder.timeTv.setText(formattedDate);




    }

    @Override
    public int getItemCount() {
        return chatArrayList.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatArrayList.get(position).getFromUid().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }



    class HolderChat extends RecyclerView.ViewHolder {

        TextView messageTv, timeTv;
        ImageView imageiv;

        public HolderChat(View itemView) {
            super(itemView);
            messageTv = itemView.findViewById(R.id.messageTV);
            imageiv = itemView.findViewById(R.id.imageiv);
            timeTv = itemView.findViewById(R.id.timeTV);
        }
    }













}





































