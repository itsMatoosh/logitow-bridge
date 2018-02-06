package com.logitow.bridge.build;

/**
 * Represents a 3 value property.
 */
public class Vec3 implements Cloneable {
    public int x = 0;
    public int y = 0;
    public int z = 0;

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "x: " + x + " y: " + y + " z: " + z;
    }

    public Vec3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 b) {
        if(b == null) return this;
        return new Vec3(this.x + b.x, this.y + b.y, this.z + b.z);
    }

    public static Vec3 zero() {
        return new Vec3(0,0,0);
    }

    public Vec3() {
    }

    @Override
    public Vec3 clone() {
        return new Vec3(this.x, this.y, this.z);
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vec3) {
            Vec3 a = (Vec3)obj;
            if(a.x == this.x && a.y == this.y && a.z == this.z) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
