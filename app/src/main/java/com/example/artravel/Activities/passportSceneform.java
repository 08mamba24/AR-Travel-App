package com.example.artravel.Activities;

import android.os.Bundle;

import com.example.artravel.GemsAdapter;
import com.example.artravel.arGemsAdapter;
import com.example.artravel.models.Gems;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.artravel.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class passportSceneform extends AppCompatActivity {

    private ArrayList mGems;
    private RecyclerView rvGems;
    private arGemsAdapter adapter;
    private int selected;

    ArFragment fragment;
    private ModelRenderable tigerRenderable,pepe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_sceneform);

        setupView();
        queryGems();

        //TODO

        // on passport details page load the flat model
        //change it from hardcoded models to links from google poly


        //stretch
        //show all gems but the ones you dont own appear grayed out
        //when you click them it gives a message about what path to complete to earn them

            setupModel();

        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            int selected;
            selected = adapter.getSelected();

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());
            createModel(anchorNode, selected);


            Toast.makeText(this, "selected is equals to "+ selected, Toast.LENGTH_SHORT).show();
        });

    }

    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this,R.raw.mesh_bengaltiger)
                .build().thenAccept(modelRenderable ->tigerRenderable = modelRenderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "cant load model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
);

        ModelRenderable.builder()
                .setSource(this,R.raw.pepe)
                .build().thenAccept(modelRenderable ->pepe = modelRenderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "cant load model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
    }


    private void createModel (AnchorNode anchorNode, int selected){

        if (selected == 0){
           TransformableNode tiger = new TransformableNode(fragment.getTransformationSystem());
           tiger.setParent(anchorNode);
           tiger.setRenderable(tigerRenderable);
           tiger.select();

        }

        if (selected == 1){
            TransformableNode human = new TransformableNode(fragment.getTransformationSystem());
            human.setParent(anchorNode);
            human.setRenderable(pepe);
            human.select();

        }
    }

    private void setupView(){

        mGems= new ArrayList<>();
        rvGems= findViewById(R.id.rvSceneForm);
        adapter = new arGemsAdapter( mGems, this.getApplicationContext());
        rvGems.setAdapter(adapter);

        rvGems.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_passport_fragment);

    }

    private void queryGems() {


        ParseUser user = ParseUser.getCurrentUser();
        if (user == null){
            Toast.makeText(this.getApplicationContext(), "user null", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this.getApplicationContext(), "user " + user.getUsername()+ " is not null", Toast.LENGTH_SHORT).show();

        ParseRelation<Gems> relation;
        relation = user.getRelation("collectedGems");
        relation.getQuery().findInBackground(new FindCallback<Gems>() {
            @Override
            public void done(List<Gems> userGems, ParseException e) {
                if (e != null) {
                    Log.e("query", "error in query");
                    e.printStackTrace();
                    return;
                }
                mGems.addAll(userGems);

                // Toast.makeText(getContext(), numCollected + " gems collected", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

            }
        });
    }

}