package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;

/**
 * Created by Michal on 23.03.2018.
 */

public class CreateQRCodeActivity extends AppCompatActivity {
    private Unbinder mUnbinder;

    @BindView(R.id.qrImage)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr_code);
        mUnbinder = ButterKnife.bind(this);
        String qr = getIntent().getStringExtra("qr");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(qr, BarcodeFormat.QR_CODE,800,800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, CreateQRCodeActivity.class);
        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.create_qr_activity_head);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
