package com.romens.extend.chart.charts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by zhoulisi on 15/1/5.
 */
public class GridChartTemplate implements Parcelable {
    private ArrayList<Child> mChildes = new ArrayList<Child>();

    public GridChartTemplate() {
    }

    public void addChild(Child child) {
        mChildes.add(child);
    }

    public Child getChild(int index) {
        return mChildes.get(index);
    }

    public ArrayList<Child> getChildes() {
        return this.mChildes;
    }

    public int getChildesCount() {
        return mChildes.size();
    }

    public void changeChildDisplay(int index, boolean enable) {
        mChildes.get(index).enableDisplay = enable;
    }

    public void changeChildWidth(int index, float width) {
        mChildes.get(index).width = width;
    }

    public void sort() {
        Collections.sort(mChildes, new Comparator<Child>() {
            @Override
            public int compare(Child lhs, Child rhs) {
                if (lhs.isFix) {
                    if (!rhs.isFix) {
                        return -1;
                    } else {
                        return comparePosition(lhs.position, rhs.position);
                    }
                } else {
                    if (rhs.isFix) {
                        return 1;
                    } else {
                        return comparePosition(lhs.position, rhs.position);
                    }
                }
            }
        });
        int size = mChildes.size();
        for (int i = 0; i < size; i++) {
            mChildes.get(i).position = i;
        }
    }

    private int comparePosition(int lhsPosition, int rhsPosition) {
        if (lhsPosition > rhsPosition) {
            return 1;
        } else if (lhsPosition == rhsPosition) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(mChildes);
    }

    public static final Creator<GridChartTemplate> CREATOR = new Creator<GridChartTemplate>() {
        public GridChartTemplate createFromParcel(Parcel in) {
            return new GridChartTemplate(in);
        }

        public GridChartTemplate[] newArray(int size) {
            return new GridChartTemplate[size];
        }
    };

    private GridChartTemplate(Parcel in) {
        mChildes.clear();
        in.readTypedList(mChildes, Child.CREATOR);
    }

    public static class Child implements Parcelable {
        public final int dataIndex;
        public final String name;
        public boolean enableDisplay = true;
        public float width;
        public int position;
        public boolean isFix = false;

        public Child(int position, int dataIndex, String name, boolean enableDisplay, boolean isFix, float width) {
            this.position = position;
            this.dataIndex = dataIndex;
            this.name = name;
            this.enableDisplay = enableDisplay;
            this.isFix = isFix;
            this.width = width;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(dataIndex);
            out.writeString(name);
            out.writeInt(enableDisplay ? 1 : 0);
            out.writeFloat(width);
            out.writeInt(position);
            out.writeInt(isFix ? 1 : 0);
        }

        public static final Creator<Child> CREATOR = new Creator<Child>() {
            public Child createFromParcel(Parcel in) {
                return new Child(in);
            }

            public Child[] newArray(int size) {
                return new Child[size];
            }
        };

        private Child(Parcel in) {
            dataIndex = in.readInt();
            name = in.readString();
            enableDisplay = in.readInt() == 1;
            width = in.readFloat();
            position = in.readInt();
            isFix = in.readInt() == 1;
        }
    }
}
