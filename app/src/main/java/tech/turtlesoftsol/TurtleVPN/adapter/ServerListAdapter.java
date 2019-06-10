package tech.turtlesoftsol.TurtleVPN.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tech.turtlesoftsol.TurtleVPN.activity.BaseActivity;
import tech.turtlesoftsol.TurtleVPN.model.Server;
import tech.turtlesoftsol.TurtleVPN.util.ConnectionQuality;
import tech.turtlesoftsol.TurtleVPN.util.CountriesNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vasilkoff on 27/09/16.
 */
public class ServerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Server> serverList = new ArrayList<Server>();
    private Context context;
    private Map<String, String> localeCountries;

    public ServerListAdapter(Context c, List<Server> serverList) {
        inflater = LayoutInflater.from(c);
        context = c;
        this.serverList =  serverList;
        localeCountries = CountriesNames.getCountries();
    }


    @Override
    public int getCount() {
        return serverList.size();
    }


    @Override
    public Server getItem(int position) {
        return serverList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {

        v = inflater.inflate(tech.turtlesoftsol.TurtleVPN.R.layout.layout_server_record_row, parent, false);

        final Server server = getItem(position);

        String code = server.getCountryShort().toLowerCase();
        if (code.equals("do"))
            code = "dom";


        ((ImageView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.imageFlag))
                .setImageResource(
                        context.getResources().getIdentifier(code,
                                "drawable",
                                context.getPackageName()));
        ((ImageView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.imageConnect))
                .setImageResource(
                        context.getResources().getIdentifier(ConnectionQuality.getConnectIcon(server.getQuality()),
                                "drawable",
                                context.getPackageName()));

        ((TextView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.textHostName)).setText(server.getHostName());
        ((TextView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.textIP)).setText(server.getIp());
        ((TextView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.textCity)).setText(server.getCity());

        String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                localeCountries.get(server.getCountryShort()) : server.getCountryLong();
        ((TextView) v.findViewById(tech.turtlesoftsol.TurtleVPN.R.id.textCountry)).setText(localeCountryName);

        if (BaseActivity.connectedServer != null && BaseActivity.connectedServer.getHostName().equals(server.getHostName())) {
            v.setBackgroundColor(ContextCompat.getColor(context, tech.turtlesoftsol.TurtleVPN.R.color.activeServer));
        }

        if (server.getType() == 1) {
            v.setBackgroundColor(ContextCompat.getColor(context, tech.turtlesoftsol.TurtleVPN.R.color.additionalServer));
        }

        return v;
    }

}
