package tech.turtlesoftsol.TurtleVPN.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import tech.turtlesoftsol.TurtleVPN.R;
import tech.turtlesoftsol.TurtleVPN.adapter.BookmarkServerListAdapter;
import tech.turtlesoftsol.TurtleVPN.model.Server;

import java.util.List;

public class BookmarkServerListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_server_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final List<Server> serverList = dbHelper.getBookmarks();
        BookmarkServerListAdapter adapter = new BookmarkServerListAdapter(serverList, this, dbHelper);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.bookmarkRv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }
}
