package utar.edu.mad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SongList extends AppCompatActivity {

    SearchView searchView;
    ListView listView;
    MyAdapter adapter;
    String mTitle[] = {"How You Like That", "Just The Way You Are", "Mojito", "Senorita", "Tales of the Red Cliff"};
    String mSinger[] = {"BlackPink", "Bruno Mars", "Jay Chou", "Shawn Mendes", "JJ Lin"};
    int images[] = {R.drawable.blackpink, R.drawable.brunomars, R.drawable.jaychou, R.drawable.shawn, R.drawable.jjlin};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        //list view
        listView = findViewById(R.id.lists);

        adapter = new MyAdapter(this, mTitle,mSinger,images);
        listView.setAdapter(adapter);

        //set item click on list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Toast.makeText(SongList.this, "BlackPink", Toast.LENGTH_SHORT).show();
                }
                if (position == 1){
                    Toast.makeText(SongList.this, "Bruno Mars", Toast.LENGTH_SHORT).show();
                }
                if (position == 2){
                    Toast.makeText(SongList.this, "Jay Chou", Toast.LENGTH_SHORT).show();
                }
                if (position == 3){
                    Toast.makeText(SongList.this, "Shawn Mendes", Toast.LENGTH_SHORT).show();
                }
                if (position == 4){
                    Toast.makeText(SongList.this, "JJ Lin", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //search view
        searchView = (SearchView) findViewById(R.id.searchSong);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                SongList.this.adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                SongList.this.adapter.getFilter().filter(s);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        //floating upload button
        FloatingActionButton fab = findViewById(R.id.btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SongList.this, "Upload", Toast.LENGTH_SHORT).show();
            }
        });

        //Bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set home page selected
        bottomNavigationView.setSelectedItemId(R.id.songList);

        //Perform ItemSelectedListener in Navigation Bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.songList:
                        return true;
                    case R.id.friendList:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chat:
//                        startActivity(new Intent(getApplicationContext(), ));
//                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(SongList.this, Profile.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rName[];
        int rImage[];

        MyAdapter (Context c, String title[], String name[], int img[]) {
            super(c, R.layout.row, R.id.name, title);
            this.context = c;
            this.rTitle = title;
            this.rName = name;
            this.rImage = img;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView songTitle = row.findViewById(R.id.title);
            TextView singerName = row.findViewById(R.id.name);

            // set resources on View
            images.setImageResource(rImage[position]);
            songTitle.setText(getItem(position));
            singerName.setText(rName[position]);

            return row;
        }
    }
}