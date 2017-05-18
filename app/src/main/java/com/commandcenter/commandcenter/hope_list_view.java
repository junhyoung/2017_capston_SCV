package com.commandcenter.commandcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static com.commandcenter.commandcenter.global.item;

/**
 * Created by kimdonghoon on 2017-05-17.
 */

public class hope_list_view extends AppCompatActivity {
    ExpandableListView listMain;
    // arrayGroup 이라는 배열리스트 생성 (상위 리스트의 제목)
    private ArrayList<String> arrayGroup = new ArrayList<String>();
    // 해쉬맵을 생성 범주별 세부 내용이 들어간다. 왼쪽은 부모, 오른쪽은 child
    private HashMap<String, ArrayList<String>> arrayChild = new HashMap<String, ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hope_list_view);

        // ArrayAdapter 생성. 아이템 View를 선택(multiple choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, item) ;
        final ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listMain = (ExpandableListView) this.findViewById(R.id.expandableListView1);
        setArrayData();

        // 아래는 아마존에서 가져온 물품 리스트의 전부
        listMain.setAdapter(new AdpMain(this, arrayGroup, arrayChild));
        // 구매희망 리스트 클릭 할 때
        listMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String menu = arrayChild.get(arrayGroup.get(groupPosition)).get(childPosition);
                Toast.makeText(hope_list_view.this, menu + "를 희망구매목록에 담았습니다.", 0).show();
                // 아이템 추가.
                item.add(menu);
                // listview 갱신
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        // 여기는 구매자가 희망리스트를 작성한 리스트 (삭제가능)
        listview.setAdapter(adapter) ;
        Button deleteButton = (Button)findViewById(R.id.delete);
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v){
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount();
                for(int i=count-1; i>=0; i--){
                    if(checkedItems.get(i)){
                        item.remove(i);
                    }
                }
                // 모든 선택 상태 초기화
                listview.clearChoices();
                adapter.notifyDataSetChanged();
            }
        });

        // selectAll button에 대한 이벤트 처리.
        Button selectAllButton = (Button)findViewById(R.id.selectAll);
        selectAllButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                int count = 0;
                count = adapter.getCount();
                for(int i=0; i<count; i++){
                    listview.setItemChecked(i, true);
                }
            }
        });
        //돌아가기 버튼 클릭 했을 때
        Button search = (Button)findViewById(R.id.back_main);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                global.changemsg = 1;
                finish();
            }
        });
    }
    private void setArrayData() {
        arrayGroup.add("Video Games");
        arrayGroup.add("Toys and Games");
        arrayGroup.add("Electronics");

        ArrayList<String> arrayCookie = new ArrayList<String>();
        arrayCookie.add("LEGO Marvel Superheroes 2 - Xbox One");
        arrayCookie.add("BenQ ZOWIE FK1+ E-Sports Ambidextrous Optical Gaming Mouse");
        arrayCookie.add("Ever Oasis - Nintendo 3DS");

        ArrayList<String> arrayFish = new ArrayList<String>();
        arrayFish.add("Simon Micro Series Game");
        arrayFish.add("Toy Story 3 The Video Game - PlayStation 2");
        arrayFish.add("동태");
        arrayFish.add("조기");
        arrayFish.add("명태");

        ArrayList<String> arrayRamen = new ArrayList<String>();
        arrayRamen.add("신라면");
        arrayRamen.add("진라면");
        arrayRamen.add("진짬뽕");

        arrayChild.put(arrayGroup.get(0), arrayCookie);
        arrayChild.put(arrayGroup.get(1), arrayFish);
        arrayChild.put(arrayGroup.get(2), arrayRamen);
    }
}
