package com.example.beautyyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ProfileFragment extends Fragment {

    TextView tvNama, tvEmail, tvKulit, tvTujuan;
    Button btnLogout;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvKulit = view.findViewById(R.id.tvKulit);
        tvTujuan = view.findViewById(R.id.tvTujuan);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvEmail.setText(user.getEmail());

            userRef = FirebaseDatabase.getInstance("https://beautyyapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("Users").child(user.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tvNama.setText(snapshot.child("nama").getValue(String.class));
                    tvKulit.setText(snapshot.child("jenis_kulit").getValue(String.class));
                    tvTujuan.setText(snapshot.child("tujuan_perawatan").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();

            Context context = v.getContext(); // cara paling aman

            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });


        return view;
    }
}
