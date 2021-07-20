package mk.ukim.finki.mpip.studentskiprasanja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter {
    // променливи за нашата листа и контекст
    private ArrayList<ChatsModel> messageModalArrayList;
    private Context context;

    // конструктор за класата
    public ChatRVAdapter(ArrayList<ChatsModel> messageModalArrayList, Context context) {
        this.messageModalArrayList = messageModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // кодот во проодлжение е за кој layout type да го врате со view holder-от
        switch (viewType) {
            case 0:
                // порака од корисникот
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_msg_item, parent, false);
                return new UserViewHolder(view);
            case 1:
                // порака од ботот
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_msg_item, parent, false);
                return new BotViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // овој метод се користи за поставување на податоци во layout фајлот
        ChatsModel modal = messageModalArrayList.get(position);
        switch (modal.getSender()) {
            case "user":
                // поставување на текстот во text vies на корисничкиот layout
                ((UserViewHolder) holder).userTV.setText(modal.getMessage());
                break;
            case "bot":
                // поставување на текстот во text view на layout на ботот
                ((BotViewHolder) holder).botTV.setText(modal.getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        // враќа големина на листа
        return messageModalArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // поставување на позиција
        switch (messageModalArrayList.get(position).getSender()) {
            case "user":
                return 0;
            case "bot":
                return 1;
            default:
                return -1;
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        // креирање на променлива за text view
        TextView userTV;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // иницијализација со id
            userTV = itemView.findViewById(R.id.idTVUser);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {

        // креирање на променлива за text view
        TextView botTV;

        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            // иницијализација со id
            botTV = itemView.findViewById(R.id.idTVBot);
        }
    }
}
