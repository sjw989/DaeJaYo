package com.example.project_4weeks_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

public class add_menu extends AppCompatActivity {
    private FirebaseStorage storage; // Firebase Storage
    private DatabaseReference databaseRef;

    private String mainImage_path; // 메뉴 메인 사진 경로 저장하는 변수
    private String mainImage_url; // 메뉴 메인 사진 다운로드 url
    private String menu_name; // 메뉴 이름
    private ArrayList<String> info = new ArrayList<>(3); // 메뉴 정보를 담을 array
    private String recipeImage_path; // 레시피 사진 경로 저장하는 변수
    private ArrayList<String> array_recipeImage_url = new ArrayList<>(); // 레시피 사진 다운로드 url을 담을 list

    private ArrayList<Ingredient> ingredient_array; // 추가될 메뉴의 재료정보 list
    private ArrayList<add_recipe> recipe_array; // 추가될 메뉴의 레시피정보 list
    private add_Ingre_Adapter ingre_adapter; // 재료정보 리사이클러뷰 어댑터
    private Add_recipe_Adapter recipe_adapter; // 레시피정보 리사이클러뷰 어댑터

    private final int GALLERY_CODE_MainImage = 1112;
    private final int GALLERY_CODE_AddRecipe_Image = 1113;

    String[] permission_list = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    }; // 갤리러 권한 요청

    // 이미지 추가
    ImageView iv_MenuImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            // 대표 이미지를 선택하는 경우
            if (requestCode == GALLERY_CODE_MainImage) {
                try {
                    Uri uri = data.getData();
                    mainImage_path = getFullPathFromUri(this, uri); // 절대경로 저장
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Toast.makeText(getApplicationContext(), "사진이 성공적으로 추가되었습니다 !", Toast.LENGTH_SHORT).show();

                    if (bitmap.getWidth() > bitmap.getHeight()) {
                        bitmap = rotate(bitmap);
                    }
                    iv_MenuImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 레시피 사진을 선택하는 경우
            else if (requestCode == GALLERY_CODE_AddRecipe_Image) {
                Uri uri = data.getData();
                recipeImage_path = getFullPathFromUri(this, uri); // 절대경로 저장
                Toast.makeText(getApplicationContext(), "사진이 성공적으로 추가되었습니다 !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 사진 회전 방지
    private Bitmap rotate(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    // 갤러리에서 선택한 사진 절대경로 구하기
    public static String getFullPathFromUri(Context ctx, Uri fileUri) {
        String fullPath = null;
        final String column = "_data";
        Cursor cursor = ctx.getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            if (document_id == null) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (column.equalsIgnoreCase(cursor.getColumnName(i))) {
                        fullPath = cursor.getString(i);
                        break;
                    }
                }
            } else {
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                final String[] projection = {column};
                try {
                    cursor = ctx.getContentResolver().query(
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            projection, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column));
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }
        return fullPath;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        iv_MenuImage = findViewById(R.id.iv_MenuImg);
        checkPermission(); // 갤러리 권한체크

        // 대표 이미지 추가 버튼
        Button btn_add_MenuImg = (Button) findViewById(R.id.btn_add_MenuImg);
        btn_add_MenuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE_MainImage);

            }
        });


        // 재료 추가 리사이클러뷰 관련 코드
        RecyclerView rec_addIngre = (RecyclerView) findViewById(R.id.rec_addIngre);
        LinearLayoutManager addIngre_layoutManager = new LinearLayoutManager(this);
        rec_addIngre.setLayoutManager(addIngre_layoutManager);
        ingredient_array = new ArrayList<>();
        ingre_adapter = new add_Ingre_Adapter(ingredient_array);
        rec_addIngre.setAdapter(ingre_adapter);
        // 재료 추가 리사이클러뷰 관련 코드 end

        // 재료 추가 버튼
        Button btn_addIngre = (Button) findViewById(R.id.btn_addIngre);
        btn_addIngre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addIngre_dialog = new AlertDialog.Builder(add_menu.this);
                View view = LayoutInflater.from(add_menu.this).inflate(R.layout.add_ingre_dialog, null, false);
                addIngre_dialog.setView(view); // 출력할 dialog에 custon dialog 적용

                Button btn_submit = (Button) view.findViewById(R.id.btn_addIngre_submit); // 추가 버튼
                EditText inputName = (EditText) view.findViewById(R.id.etv_addIngre_dialog_name);
                EditText inputCount = (EditText) view.findViewById(R.id.etv_addIngre_dialog_count);
                EditText inputUnit = (EditText) view.findViewById(R.id.etv_addIngre_dialog_unit);

                AlertDialog dialog = addIngre_dialog.create();
                dialog.show(); // dialog show

                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = inputName.getText().toString();
                        String count = inputCount.getText().toString();
                        String unit = inputUnit.getText().toString();
                        Ingredient new_ingredient = new Ingredient(Integer.toString(ingredient_array.size() + 1), name, count, unit);
                        ingredient_array.add(new_ingredient);
                        ingre_adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

            }
        });

        // 재료 삭제 버튼
        Button btn_deleteIngre = (Button) findViewById(R.id.btn_deleteIngre);
        btn_deleteIngre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ingredient_array.size() > 0) {
                    AlertDialog.Builder deleteIngre_dialog = new AlertDialog.Builder(add_menu.this);
                    View view = LayoutInflater.from(add_menu.this).inflate(R.layout.delete_ingre_dialog, null, false);
                    deleteIngre_dialog.setView(view); // 출력할 dialog에 custon dialog 적용

                    Button btn_submit = (Button) view.findViewById(R.id.btn_delete_submit); // 삭제 버튼
                    EditText delete_num = (EditText) view.findViewById(R.id.etv_deleteIngre_dialog_num);
                    AlertDialog dialog = deleteIngre_dialog.create();
                    dialog.show();

                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(delete_num.getText())) {
                                int num = Integer.parseInt(delete_num.getText().toString());
                                if (num <= 0 || num > ingredient_array.size()) {
                                    Toast.makeText(getApplicationContext(), "잘못된 숫자를 입력하셨습니다.\n 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                                } else {
                                    ingredient_array.remove(num - 1);
                                    ingre_adapter.notifyItemRemoved(num - 1);
                                    for (int i = 0; i < ingredient_array.size(); i++) {
                                        int cur_num = Integer.parseInt(ingredient_array.get(i).getIngre_num());
                                        if (cur_num > num) {
                                            ingredient_array.get(i).setIngre_num(Integer.toString(cur_num - 1));
                                        }
                                    }
                                    ingre_adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "삭제할 재료가 없습니다 !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // 레시피 추가 리사이클러뷰 관련 코드
        RecyclerView rec_addRecipe = (RecyclerView) findViewById(R.id.rec_addRecipe);
        LinearLayoutManager addRecipe_layoutManager = new LinearLayoutManager(this);
        rec_addRecipe.setLayoutManager(addRecipe_layoutManager);
        recipe_array = new ArrayList<>();
        recipe_adapter = new Add_recipe_Adapter(recipe_array);
        rec_addRecipe.setAdapter(recipe_adapter);
        // 레시피 추가 리사이클러뷰 관련 코드 end


        // 레시피 추가 버튼
        Button btn_addRecipe = (Button) findViewById(R.id.btn_addRecipe);
        btn_addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addRecipe_dialog = new AlertDialog.Builder(add_menu.this);
                View view = LayoutInflater.from(add_menu.this).inflate(R.layout.add_recipe_dialog, null, false);
                addRecipe_dialog.setView(view);

                Button btn_submit = (Button) view.findViewById(R.id.btn_addRecipe_submit); // 추가 버튼
                EditText inputTxT = (EditText) view.findViewById(R.id.etv_addRecipe_dialog_txt);
                AlertDialog dialog = addRecipe_dialog.create();
                dialog.show();

                // 레시피 사진 추가 버튼
                Button btn_addRecipe_Img = (Button) view.findViewById(R.id.btn_addRecipe_Img);
                btn_addRecipe_Img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, ""), GALLERY_CODE_AddRecipe_Image);
                    }
                });


                btn_submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recipeImage_path == null) {
                            Toast.makeText(getApplicationContext(), "이미지를 선택해주세요 !", Toast.LENGTH_SHORT).show();
                        } else {
                            if (TextUtils.isEmpty(inputTxT.getText())) {
                                Toast.makeText(getApplicationContext(), "조리과정을 작성해주세요 !", Toast.LENGTH_SHORT).show();
                            } else {
                                String txt = inputTxT.getText().toString();
                                add_recipe new_recipe = new add_recipe(Integer.toString(recipe_array.size() + 1), recipeImage_path, txt);
                                recipe_array.add(new_recipe);
                                recipe_adapter.notifyDataSetChanged();
                                recipeImage_path = null;
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        // 레시피 삭제 버튼
        Button btn_deleteRecipe = (Button) findViewById(R.id.btn_deleteRecipe);
        btn_deleteRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipe_array.size() > 0) {
                    AlertDialog.Builder deleteIngre_dialog = new AlertDialog.Builder(add_menu.this);
                    View view = LayoutInflater.from(add_menu.this).inflate(R.layout.delete_recipe_dialog, null, false);
                    deleteIngre_dialog.setView(view); // 출력할 dialog에 custon dialog 적용

                    Button btn_submit = (Button) view.findViewById(R.id.btn_delete_submit); // 삭제 버튼
                    EditText delete_num = (EditText) view.findViewById(R.id.etv_deleteRecipe_dialog_num);
                    AlertDialog dialog = deleteIngre_dialog.create();
                    dialog.show();
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(delete_num.getText())) {
                                int num = Integer.parseInt(delete_num.getText().toString());
                                if (num <= 0 || num > recipe_array.size()) {
                                    Toast.makeText(getApplicationContext(), "잘못된 숫자를 입력하셨습니다.\n 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                                } else {
                                    recipe_array.remove(num - 1);
                                    recipe_adapter.notifyItemRemoved(num - 1);
                                    for (int i = 0; i < recipe_array.size(); i++) {
                                        int cur_num = Integer.parseInt(recipe_array.get(i).getNum());
                                        if (cur_num > num) {
                                            recipe_array.get(i).setNum(Integer.toString(cur_num - 1));
                                        }
                                    }

                                    recipe_adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "삭제할 레시피가 없습니다 !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*
        메뉴 등록 버튼 로직
        1. 버튼 click 이벤트 발생
        --> 대표 이미지 || 메뉴 이름 || 레시피정보 || 재료정보
            이 중에 하나라도 빠지는게 있다면, Toast 로 알려줌

        2. 위 항목이 모두 입력되어 있으면
            1) 디바이스 절대경로를 이용하여 대표이미지 파이어베이스스토리지에 업로드
            2) 대표이미지 스토리지에 업로드 성공할 시
               --> 대표이미지 다운로드 url 을 생성
               --> 레시피 개수만큼 for 돌면서 디바이스 절대경로를 이용하여 레시피 사진 스토리지에 업로드
                  --> for 문을 돌면서 레시피 사진이 스토리지에 업로드 될 때 마다 레시피 객체가 가지고 있던 절대경로를 다운로드 url로 변환해줌
                  --> 사용자가 입력한 정보를 바탕으로 new_menu 객체를 생성해서 DB에 업로드함

                      <!!!> for 문이 종료된 후에 new_menu 객체를 만들어서 업로드 안 하고 이렇게 레시피 개수만큼 객체를 만들어서 DB에 업로드한 이유는,
                            onSuccessListener 가 for 문이 끝날 때까지 실행이 안 되는 경우가 있어서 DB에 최종적인 내용이 안 올라감.
                            ex) DB에
                                레시피1 = img : "다운로드 url"
                                레시피2 = img :  절대경로
                             이런식으로 저장됨. 그래서 for 문을 돌면서 매번 new_menu 객체를 만들어서 덮어쓰기? 하는 느낌으로

        3. 2번 마지막줄에 나와있는 것처럼, 사용자가 메뉴추가를 하고나서 다시 select_menu 액티비티로 돌아왔을 때,
           레시피 사진 등록 onSuccessListener 가 아직 실행되지 않아 메뉴 업데이트가 안되어있음.
           앱 재실행하면 업데이트 되는 정도의 시간차이. --> 사실 이것도 레시피 길이가 길어지면 어떻게 될지 모르겠음

        */

        // 메뉴 등록 버튼
        Button btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이름 저장
                EditText etv_addmenu_inputName = (EditText) findViewById(R.id.etv_addmenu_inputName);
                menu_name = etv_addmenu_inputName.getText().toString();


                // 메뉴정보 저장
                EditText etv_addmenu_inputInfo1 = (EditText) findViewById(R.id.etv_addmenu_inputInfo1);
                String info1 = etv_addmenu_inputInfo1.getText().toString();
                EditText etv_addmenu_inputInfo2 = (EditText) findViewById(R.id.etv_addmenu_inputInfo2);
                String info2 = etv_addmenu_inputInfo2.getText().toString();
                EditText etv_addmenu_inputInfo3 = (EditText) findViewById(R.id.etv_addmenu_inputInfo3);
                String info3 = etv_addmenu_inputInfo3.getText().toString();

                // 재료 정보는 IngredientArray에 있음
                // 레시피 정보는 IngredientArray에 있음


                // storage 참조 얻기
                storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://project-daejayo.appspot.com");

                // 메인 사진 Firebase Storage에 저장
                if (mainImage_path == null) {
                    Toast.makeText(getApplicationContext(), "대표 이미지를 등록하지 않았어요 !", Toast.LENGTH_SHORT).show();
                }
                else if(menu_name == null){
                    Toast.makeText(getApplicationContext(), "메뉴 이름을 입력하지 않았어요 !", Toast.LENGTH_SHORT).show();
                }
                else if(recipe_array.size() == 0){
                    Toast.makeText(getApplicationContext(), "레시피 정보를 등록하지 않았어요 !", Toast.LENGTH_SHORT).show();
                }
                else if(ingredient_array.size() == 0){
                    Toast.makeText(getApplicationContext(), "재료 정보를 등록하지 않았어요 !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Uri main_file = Uri.fromFile(new File(mainImage_path));
                    StorageReference riversRef = storageRef.child("images/" + main_file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(main_file); // 사진 업로드
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "메인 사진 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final StorageReference ref = storageRef.child("images/" + main_file.getLastPathSegment());
                            UploadTask uploadTask = ref.putFile(main_file);
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()){
                                        throw task.getException();
                                    }
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri downloadUri = task.getResult();
                                        mainImage_url = downloadUri.toString();

                                        // 레시피 사진 Firebase Storage에 저장
                                        for (int i = 0; i < recipe_array.size(); i++) { // 추가된 레시피 단계 개수만큼 반복
                                            int idx = i;
                                            // Firebase Storage에 레시피 사진 업로드
                                            Uri recipe_file = Uri.fromFile(new File(recipe_array.get(idx).getImg()));
                                            StorageReference recipe_riversRef = storageRef.child("images/" + recipe_file.getLastPathSegment());
                                            UploadTask recipe_uploadTask = recipe_riversRef.putFile(recipe_file);

                                            // 레시피의 String img를 절대경로 --> 사진이 저장된 url로 바꿔줌
                                            recipe_uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    storageRef.child("images/").child(recipe_file.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            recipe_array.get(idx).setImg(uri.toString());

                                                            // 추가될 새로운 menu 객체 생성
                                                            Information info = new Information(info1,info2,info3);
                                                            New_menu new_menu = new New_menu(menu_name, mainImage_url,Integer.toString(select_menu.curCategory_size), info, ingredient_array, recipe_array);
                                                            // DB update
                                                            databaseRef = FirebaseDatabase.getInstance().getReference(); // database 참조 객체
                                                            databaseRef.child(MainActivity.selected_category_ENG).child(Integer.toString(select_menu.curCategory_size - 1)).setValue(new_menu); // child 생성
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), "레시피 path -> url 변경 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(),"레시피 사진 등록 실패했습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } // 레시피 사진 저장 end
                                        finish();
                                    }
                                }
                            });
                        }
                    }); // 메인 사진 저장 end
                }
            }

        });


    }


    // back 버튼
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return true;
    }


    // 갤러리 권한 얻기
    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < grantResults.length; i++) {
                //허용됬다면
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "앱권한설정하세요", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}