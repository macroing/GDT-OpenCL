/**
 * Copyright 2009 - 2021 J&#246;rgen Lundgren
 * 
 * This file is part of org.macroing.gdt.openrc.
 * 
 * org.macroing.gdt.openrc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.macroing.gdt.openrc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.macroing.gdt.openrc. If not, see <http://www.gnu.org/licenses/>.
 */
package org.macroing.gdt.openrc.geometry;

import static org.macroing.gdt.openrc.Mathematics.cos;
import static org.macroing.gdt.openrc.Mathematics.sin;
import static org.macroing.gdt.openrc.Mathematics.sqrt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.macroing.gdt.openrc.Constants;

/**
 * The values in the {@code float} array {@code array} consists of the following:
 * <ol>
 * <li>Eye X</li>
 * <li>Eye Y</li>
 * <li>Eye Z</li>
 * <li>Up X</li>
 * <li>Up Y</li>
 * <li>Up Z</li>
 * <li>Look-at X</li>
 * <li>Look-at Y</li>
 * <li>Look-at Z</li>
 * <li>OrthoNormalBasis-U X</li>
 * <li>OrthoNormalBasis-U Y</li>
 * <li>OrthoNormalBasis-U Z</li>
 * <li>OrthoNormalBasis-V X</li>
 * <li>OrthoNormalBasis-V Y</li>
 * <li>OrthoNormalBasis-V Z</li>
 * <li>OrthoNormalBasis-W X</li>
 * <li>OrthoNormalBasis-W Y</li>
 * <li>OrthoNormalBasis-W Z</li>
 * <li>View-plane distance</li>
 * <li>Zoom</li>
 * </ol>
 * 
 * @since 1.0.0
 * @author J&#246;rgen Lundgren
 */
public final class Camera {
	public static final int ABSOLUTE_OFFSET_OF_EYE = 0;
	public static final int ABSOLUTE_OFFSET_OF_LOOK_AT = 6;
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U = 9;
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V = 12;
	public static final int ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W = 15;
	public static final int ABSOLUTE_OFFSET_OF_UP = 3;
	public static final int ABSOLUTE_OFFSET_OF_VIEW_PLANE_DISTANCE = 18;
	public static final int ABSOLUTE_OFFSET_OF_ZOOM = 19;
	public static final int SIZE = 3 + 3 + 3 + 3 + 3 + 3 + 1 + 1;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final CameraPredicate cameraPredicate;
	private final float[] array = new float[SIZE];
	private Scene scene;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Camera() {
		this((x, y, z, scene) -> new boolean[] {true, true, true});
	}
	
	public Camera(final CameraPredicate cameraPredicate) {
		this.cameraPredicate = Objects.requireNonNull(cameraPredicate, "cameraPredicate == null");
		
		setEye(500.0F, 0.0F, 500.0F);
		setUp(0.0F, 1.0F, 0.0F);
		setLookAt(0.0F, 0.0F, 0.0F);
		setViewPlaneDistance(800.0F / Constants.WIDTH_SCALE);
		setZoom(1.0F);
		calculateOrthonormalBasis();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public float getEyeX() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 0];
	}
	
	public float getEyeY() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 1];
	}
	
	public float getEyeZ() {
		return this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
	}
	
	public float getLookAtX() {
		return this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0];
	}
	
	public float getLookAtY() {
		return this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1];
	}
	
	public float getLookAtZ() {
		return this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2];
	}
	
	public float getOrthoNormalBasisUX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 0];
	}
	
	public float getOrthoNormalBasisUY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 1];
	}
	
	public float getOrthoNormalBasisUZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U + 2];
	}
	
	public float getOrthoNormalBasisVX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 0];
	}
	
	public float getOrthoNormalBasisVY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 1];
	}
	
	public float getOrthoNormalBasisVZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V + 2];
	}
	
	public float getOrthoNormalBasisWX() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 0];
	}
	
	public float getOrthoNormalBasisWY() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 1];
	}
	
	public float getOrthoNormalBasisWZ() {
		return this.array[ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W + 2];
	}
	
	public float getUpX() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 0];
	}
	
	public float getUpY() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 1];
	}
	
	public float getUpZ() {
		return this.array[ABSOLUTE_OFFSET_OF_UP + 2];
	}
	
	public float getViewPlaneDistance() {
		return this.array[ABSOLUTE_OFFSET_OF_VIEW_PLANE_DISTANCE];
	}
	
	public float getZoom() {
		return this.array[ABSOLUTE_OFFSET_OF_ZOOM];
	}
	
	public float[] getArray() {
		return this.array;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	@Override
	public String toString() {
		return String.format("Camera: [Eye=%s,%s,%s], [Up=%s,%s,%s], [LookAt=%s,%s,%s], [OrthoNormalBasisU=%s,%s,%s], [OrthoNormalBasisV=%s,%s,%s], [OrthoNormalBasisW=%s,%s,%s], [ViewPlaneDistance=%s]", Float.toString(getEyeX()), Float.toString(getEyeY()), Float.toString(getEyeZ()), Float.toString(getUpX()), Float.toString(getUpY()), Float.toString(getUpZ()), Float.toString(getLookAtX()), Float.toString(getLookAtY()), Float.toString(getLookAtZ()), Float.toString(getOrthoNormalBasisUX()), Float.toString(getOrthoNormalBasisUY()), Float.toString(getOrthoNormalBasisUZ()), Float.toString(getOrthoNormalBasisVX()), Float.toString(getOrthoNormalBasisVY()), Float.toString(getOrthoNormalBasisVZ()), Float.toString(getOrthoNormalBasisWX()), Float.toString(getOrthoNormalBasisWY()), Float.toString(getOrthoNormalBasisWZ()), Float.toString(getViewPlaneDistance()));
	}
	
	public void calculateOrthonormalBasis() {
		Vector.subtract(this.array, ABSOLUTE_OFFSET_OF_EYE, this.array, ABSOLUTE_OFFSET_OF_LOOK_AT, this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W);
		Vector.normalize(this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W);
		Vector.crossProduct(this.array, ABSOLUTE_OFFSET_OF_UP, this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W, this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U);
		Vector.normalize(this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U);
		Vector.crossProduct(this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_W, this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_U, this.array, ABSOLUTE_OFFSET_OF_ORTHONORMAL_BASIS_V);
	}
	
	public void lookDown(final float distance) {
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1] += distance;
		
		calculateOrthonormalBasis();
	}
	
	public void moveBackward(final float distance) {
		final float eyeX = this.array[ABSOLUTE_OFFSET_OF_EYE + 0];
		final float eyeY = this.array[ABSOLUTE_OFFSET_OF_EYE + 1];
		final float eyeZ = this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
		
		final float lookAtX = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0];
		final float lookAtY = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1];
		final float lookAtZ = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2];
		
		final float x0 = lookAtX - eyeX;
		final float y0 = lookAtY - eyeY;
		final float z0 = lookAtZ - eyeZ;
		
		final float lengthReciprocal = 1.0F / sqrt(x0 * x0 + y0 * y0 + z0 * z0);
		
		final float x1 = x0 * lengthReciprocal;
		final float y1 = y0 * lengthReciprocal;
		final float z1 = z0 * lengthReciprocal;
		
		final float x2 = eyeX + distance * x1;
		final float y2 = eyeY + distance * y1;
		final float z2 = eyeZ + distance * z1;
		
		final boolean[] test = this.cameraPredicate.test(x2, y2, z2, this.scene);
		
		final boolean testX = test[0];
		final boolean testY = test[1];
		final boolean testZ = test[2];
		
		if(testX || testY || testZ) {
			if(testX) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 0] = x2;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0] += distance * x1;
			}
			
			if(testY) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 1] = y2;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1] += distance * y1;
			}
			
			if(testZ) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 2] = z2;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2] += distance * z1;
			}
			
			calculateOrthonormalBasis();
		}
	}
	
	public void moveLeft(final float distance) {
		final float eyeX = this.array[ABSOLUTE_OFFSET_OF_EYE + 0];
		final float eyeY = this.array[ABSOLUTE_OFFSET_OF_EYE + 1];
		final float eyeZ = this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
		
		final float lookAtX = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0];
		final float lookAtY = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1];
		final float lookAtZ = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2];
		
		final float upX = this.array[ABSOLUTE_OFFSET_OF_UP + 0];
		final float upY = this.array[ABSOLUTE_OFFSET_OF_UP + 1];
		final float upZ = this.array[ABSOLUTE_OFFSET_OF_UP + 2];
		
		final float x0 = lookAtX - eyeX;
		final float y0 = lookAtY - eyeY;
		final float z0 = lookAtZ - eyeZ;
		
		final float lengthReciprocal = 1.0F / sqrt(x0 * x0 + y0 * y0 + z0 * z0);
		
		final float x1 = x0 * lengthReciprocal;
		final float y1 = y0 * lengthReciprocal;
		final float z1 = z0 * lengthReciprocal;
		
		final float x2 = upY * z1 - upZ * y1;
		final float y2 = upZ * x1 - upX * z1;
		final float z2 = upX * y1 - upY * x1;
		
		final float x3 = eyeX + distance * x2;
		final float y3 = eyeY + distance * y2;
		final float z3 = eyeZ + distance * z2;
		
		final boolean[] test = this.cameraPredicate.test(x3, y3, z3, this.scene);
		
		final boolean testX = test[0];
		final boolean testY = test[1];
		final boolean testZ = test[2];
		
		if(testX || testY || testZ) {
			if(testX) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 0] = x3;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0] += distance * x2;
			}
			
			if(testY) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 1] = y3;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1] += distance * y2;
			}
			
			if(testZ) {
				this.array[ABSOLUTE_OFFSET_OF_EYE + 2] = z3;
				this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2] += distance * z2;
			}
			
			calculateOrthonormalBasis();
		}
	}
	
	public void rotateX(final float angleX) {
		final float cosAngleX = cos(angleX);
		final float sinAngleX = sin(angleX);
		
		final float eyeY = this.array[ABSOLUTE_OFFSET_OF_EYE + 1];
		final float eyeZ = this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
		
		final float lookAtY = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1];
		final float lookAtZ = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2];
		
		final float y0 = lookAtY - eyeY;
		final float z0 = lookAtZ - eyeZ;
		final float y1 = y0 * cosAngleX - z0 * sinAngleX;
		final float z1 = y0 * sinAngleX + z0 * cosAngleX;
		
		final float lengthReciprocal = 1.0F / sqrt(y1 * y1 + z1 * z1);
		
		final float y2 = y1 * lengthReciprocal + eyeY;
		final float z2 = z1 * lengthReciprocal + eyeZ;
		
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1] = y2;
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2] = z2;
		
		calculateOrthonormalBasis();
	}
	
	public void rotateY(final float angleY) {
		final float cosAngleY = cos(angleY);
		final float sinAngleY = sin(angleY);
		
		final float eyeX = this.array[ABSOLUTE_OFFSET_OF_EYE + 0];
		final float eyeZ = this.array[ABSOLUTE_OFFSET_OF_EYE + 2];
		
		final float lookAtX = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0];
		final float lookAtZ = this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2];
		
		final float x0 = lookAtX - eyeX;
		final float z0 = lookAtZ - eyeZ;
		final float x1 = x0 * cosAngleY - z0 * sinAngleY;
		final float z1 = x0 * sinAngleY + z0 * cosAngleY;
		
		final float lengthReciprocal = 1.0F / sqrt(x1 * x1 + z1 * z1);
		
		final float x2 = x1 * lengthReciprocal + eyeX;
		final float z2 = z1 * lengthReciprocal + eyeZ;
		
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0] = x2;
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2] = z2;
		
		calculateOrthonormalBasis();
	}
	
	public void setEye(final float x, final float y, final float z) {
		final boolean[] test = this.cameraPredicate.test(x, y, z, this.scene);
		
		final boolean testX = test[0];
		final boolean testY = test[1];
		final boolean testZ = test[2];
		
		if(testX) {
			this.array[ABSOLUTE_OFFSET_OF_EYE + 0] = x;
		}
		
		if(testY) {
			this.array[ABSOLUTE_OFFSET_OF_EYE + 1] = y;
		}
		
		if(testZ) {
			this.array[ABSOLUTE_OFFSET_OF_EYE + 2] = z;
		}
	}
	
	public void setLookAt(final float x, final float y, final float z) {
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 0] = x;
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 1] = y;
		this.array[ABSOLUTE_OFFSET_OF_LOOK_AT + 2] = z;
	}
	
	public void setScene(final Scene scene) {
		this.scene = Objects.requireNonNull(scene, "scene == null");
	}
	
	public void setUp(final float x, final float y, final float z) {
		this.array[ABSOLUTE_OFFSET_OF_UP + 0] = x;
		this.array[ABSOLUTE_OFFSET_OF_UP + 1] = y;
		this.array[ABSOLUTE_OFFSET_OF_UP + 2] = z;
	}
	
	public void setViewPlaneDistance(final float distance) {
		this.array[ABSOLUTE_OFFSET_OF_VIEW_PLANE_DISTANCE] = distance;
	}
	
	public void setZoom(final float zoom) {
		this.array[ABSOLUTE_OFFSET_OF_ZOOM] = zoom;
	}
	
	public void write(final DataOutput dataOutput) {
		try {
			dataOutput.writeFloat(getEyeX());
			dataOutput.writeFloat(getEyeY());
			dataOutput.writeFloat(getEyeZ());
			dataOutput.writeFloat(getUpX());
			dataOutput.writeFloat(getUpY());
			dataOutput.writeFloat(getUpZ());
			dataOutput.writeFloat(getLookAtX());
			dataOutput.writeFloat(getLookAtY());
			dataOutput.writeFloat(getLookAtZ());
			dataOutput.writeFloat(getViewPlaneDistance());
			dataOutput.writeFloat(getZoom());
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void write(final File file) {
		try(final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			write(dataOutputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static Camera read(final DataInput dataInput) {
		try {
			final
			Camera camera = new Camera();
			camera.setEye(dataInput.readFloat(), dataInput.readFloat(), dataInput.readFloat());
			camera.setUp(dataInput.readFloat(), dataInput.readFloat(), dataInput.readFloat());
			camera.setLookAt(dataInput.readFloat(), dataInput.readFloat(), dataInput.readFloat());
			camera.setViewPlaneDistance(dataInput.readFloat());
			camera.setZoom(dataInput.readFloat());
			camera.calculateOrthonormalBasis();
			
			return camera;
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static Camera read(final File file) {
		try(final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			return read(dataInputStream);
		} catch(final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}