package com.example.hp.mywechat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class wexinFragment extends Fragment {


    public wexinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.tab01, container, false);
    }
    private void initexpandData(){
        Uri uri= ContactsContract.Contacts.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor=resolver.query(uri, null, null, null, null);
        if(cursor!=null){
            while(cursor.moveToNext()){

                int idFieldIndex=cursor.getColumnIndex("_id");
                int id=cursor.getInt(idFieldIndex);


                int nameFieldIndex  = cursor.getColumnIndex("display_name");
                String name=cursor.getString(nameFieldIndex);

                int numCountFieldIndex=cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                int numCount=cursor.getInt(numCountFieldIndex);
                String phoneNumber="";
                if(numCount>0){

                    Cursor phonecursor=getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            new String[]{Integer.toString(id)}, null);
                    if(phonecursor!=null){
                        if(phonecursor.moveToFirst()){
                            int numFieldIndex=phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            phoneNumber=phonecursor.getString(numFieldIndex);
                        }
                        phonecursor.close();
                    }
                }
                item=new HashMap<String,String>();  //必须循环创建
                item.put("name", name);
                item.put("phoneNumber", phoneNumber);
                data.add(item);
            }
            cursor.close();
        }
    }


}
