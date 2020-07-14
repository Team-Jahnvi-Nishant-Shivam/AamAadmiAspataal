package com.nsh.covid19.hospital.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nsh.covid19.hospital.R;
import com.nsh.covid19.hospital.dip.ConvolutionFilters;
import com.nsh.covid19.hospital.dip.Historic;
import com.nsh.covid19.hospital.dip.ImageEffects;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UploadActivity extends AppCompatActivity implements OnTouchListener, View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int NONE = 0;
    private int mode = NONE;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private Bitmap resultBitmap;

    private ImageView imageView;

    private ImageEffects imageEffects = new ImageEffects();
    private Historic historicClass = new Historic();
    private ConvolutionFilters convolutionClass = new ConvolutionFilters();
    private ProgressBar progress;

    private TextView brightnessTv, contrastTv;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;

    private float[] lastEvent = null;
    private SeekBar brightnessSeekBar, contrastSeekBar;

    @SuppressWarnings("ConstantConditions")
    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        imageView = findViewById(R.id.image);
        imageView.setDrawingCacheEnabled(true);

        brightnessSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);
        contrastSeekBar = (SeekBar) findViewById(R.id.contrastSeekBar);

        brightnessTv = (TextView) findViewById(R.id.brightTv);
        contrastTv = (TextView) findViewById(R.id.contrastTv);

        findViewById(R.id.undo).setOnClickListener(this);
        findViewById(R.id.undo1).setOnClickListener(this);
        findViewById(R.id.undo2).setOnClickListener(this);
        findViewById(R.id.undo3).setOnClickListener(this);
        findViewById(R.id.undo4).setOnClickListener(this);
        findViewById(R.id.undo5).setOnClickListener(this);
        findViewById(R.id.undo6).setOnClickListener(this);

        findViewById(R.id.greyscale).setOnClickListener(this);
        findViewById(R.id.edge).setOnClickListener(this);
        findViewById(R.id.gaussian).setOnClickListener(this);
        findViewById(R.id.sharpen).setOnClickListener(this);
        findViewById(R.id.invert).setOnClickListener(this);
        findViewById(R.id.sepia).setOnClickListener(this);
        findViewById(R.id.relief).setOnClickListener(this);

        progress = (ProgressBar) findViewById(R.id.progressBar1);
        progress.setVisibility(ProgressBar.INVISIBLE);

        imageView.setOnTouchListener(this);
        imageView.setClickable(true);

        Picasso.get().load(getIntent().getStringExtra("image")).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bmp, Picasso.LoadedFrom from) {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageBitmap(bmp);
                matrix = imageView.getImageMatrix();

                historicClass.setNewHistoric(bmp);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                progress.setVisibility(ProgressBar.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = imageEffects.Brightness(historicClass.getCurrentHistoric(), brightnessSeekBar.getProgress() * 50 - 250);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progress.setVisibility(ProgressBar.INVISIBLE);
                                imageView.setImageBitmap(resultBitmap);
                            }
                        });

                    }
                }).start();


            }

        });
        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                progress.setVisibility(ProgressBar.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        resultBitmap = imageEffects.Contrast(historicClass.getCurrentHistoric(), contrastSeekBar.getProgress() + 1);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                progress.setVisibility(ProgressBar.INVISIBLE);
                                imageView.setImageBitmap(resultBitmap);

                            }
                        });

                    }
                }).start();


            }

        });

    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float newRot;
        // handle touch events here
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (imageView.getWidth() / 2) * sx;
                        float yc = (imageView.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
        }

        imageView.setImageMatrix(matrix);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.greyscale:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = imageEffects.Greyscale(historicClass.getCurrentHistoric());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    imageView.setImageBitmap(resultBitmap);
                                    progress.setVisibility(ProgressBar.INVISIBLE);
                                }
                            });
                            historicClass.setNewHistoric(imageEffects.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edge:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.EDGE_DETECT);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    imageView.setImageBitmap(resultBitmap);
                                    progress.setVisibility(ProgressBar.INVISIBLE);

                                }
                            });
                            historicClass.setNewHistoric(convolutionClass.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sharpen:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.SHARP);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    imageView.setImageBitmap(resultBitmap);
                                    progress.setVisibility(ProgressBar.INVISIBLE);

                                }
                            });
                            historicClass.setNewHistoric(convolutionClass.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();

                }

                break;
            case R.id.gaussian:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.BLUR);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    imageView.setImageBitmap(resultBitmap);
                                    progress.setVisibility(ProgressBar.INVISIBLE);

                                }
                            });
                            historicClass.setNewHistoric(convolutionClass.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.invert:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = imageEffects.Invert(historicClass.getCurrentHistoric());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    progress.setVisibility(ProgressBar.INVISIBLE);
                                    imageView.setImageBitmap(resultBitmap);
                                }
                            });
                            historicClass.setNewHistoric(imageEffects.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.sepia:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = imageEffects.SepiaEffect(historicClass.getCurrentHistoric());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    progress.setVisibility(ProgressBar.INVISIBLE);
                                    imageView.setImageBitmap(resultBitmap);
                                }
                            });
                            historicClass.setNewHistoric(imageEffects.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.relief:
                if (Historic.hist != -1) {
                    progress.setVisibility(ProgressBar.VISIBLE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            resultBitmap = convolutionClass.Filter(historicClass.getCurrentHistoric(), ConvolutionFilters.filterType.RELIEF);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    imageView.setImageBitmap(resultBitmap);
                                    progress.setVisibility(ProgressBar.INVISIBLE);

                                }
                            });
                            historicClass.setNewHistoric(convolutionClass.setBmp());
                        }
                    }).start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Open an Image!", Toast.LENGTH_SHORT).show();

                }

                break;

            case R.id.undo:
            case R.id.undo1:
            case R.id.undo2:
            case R.id.undo3:
            case R.id.undo4:
            case R.id.undo5:
            case R.id.undo6:
                if (Historic.hist != -1) {
                    imageView.setImageBitmap(historicClass.getLastHistoric());
                }
                break;
        }
    }
}
