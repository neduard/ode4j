/*************************************************************************
 *                                                                       *
 * Open Dynamics Engine, Copyright (C) 2001,2002 Russell L. Smith.       *
 * All rights reserved.  Email: russ@q12.org   Web: www.q12.org          *
 *                                                                       *
 * This library is free software; you can redistribute it and/or         *
 * modify it under the terms of EITHER:                                  *
 *   (1) The GNU Lesser General Public License as published by the Free  *
 *       Software Foundation; either version 2.1 of the License, or (at  *
 *       your option) any later version. The text of the GNU Lesser      *
 *       General Public License is included with this library in the     *
 *       file LICENSE.TXT.                                               *
 *   (2) The BSD-style license that is included with this library in     *
 *       the file LICENSE-BSD.TXT.                                       *
 *                                                                       *
 * This library is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files    *
 * LICENSE.TXT and LICENSE-BSD.TXT for more details.                     *
 *                                                                       *
 *************************************************************************/
package org.ode4j.ode.internal.joints;

import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DLMotorJoint;
import org.ode4j.ode.internal.DxWorld;
import static org.ode4j.ode.OdeMath.*;


/**
 * ****************************************************************************
 * lmotor joint
 */
public class DxJointLMotor extends DxJoint implements DLMotorJoint {
	private int num;
	private final int[] _rel = new int[3];
	private final DVector3[] axis = new DVector3[3];
	private final DxJointLimitMotor[] limot = new DxJointLimitMotor[3];


	DxJointLMotor( DxWorld w ) 
	//dxJoint( w )
	{
		super(w);
		int i;
		num = 0;
		for ( i = 0;i < 3;i++ )
		{
			//dSetZero( axis[i], 4 );
			axis[i] = new DVector3();
			limot[i] = new DxJointLimitMotor();
			limot[i].init( world );
		}
	}

	void
	//computeGlobalAxes( dVector3 ax[3] )
	computeGlobalAxes( DVector3[] ax )
	{
		for ( int i = 0; i < num; i++ )
		{
			if ( _rel[i] == 1 )
			{
				dMULTIPLY0_331( ax[i], node[0].body._posr.R, axis[i] );
			}
			else if ( _rel[i] == 2 )
			{
				if ( node[1].body!= null )   // jds: don't assert, just ignore
				{
					dMULTIPLY0_331( ax[i], node[1].body._posr.R, axis[i] );
				}
			}
			else
			{
				ax[i].set(axis[i]);
				//            ax[i][0] = axis[i][0];
				//            ax[i][1] = axis[i][1];
				//            ax[i][2] = axis[i][2];
			}
		}
	}

	public void
	getInfo1( DxJoint.Info1 info )
	{
		info.setM(0);
		info.setNub(0);
		for ( int i = 0; i < num; i++ )
		{
			if ( limot[i].fmax > 0 )
			{
				info.incM();
			}
		}
	}

	public void
	getInfo2( DxJoint.Info2 info )
	{
		int row = 0;
		DVector3[] ax = new DVector3[3];
		for (int i = 0; i<3; i++ ) {
			ax[i] = new DVector3();
		}

		computeGlobalAxes( ax );

		for ( int i = 0;i < num;i++ )
		{
			row += limot[i].addLimot( this, info, row, ax[i], false );
		}
	}

	//void dJointSetLMotorAxis( dJoint j, int anum, int rel, dReal x, dReal y, dReal z )
	public void dJointSetLMotorAxis( int anum, int rel, double x, double y, double z )
	{
		dJointSetLMotorAxis(anum, rel, new DVector3(x, y, z));
	}
	
	public void dJointSetLMotorAxis( int anum, int rel, DVector3C r )
	{
		//    dxJointLMotor joint = ( dxJointLMotor )j;
		//for now we are ignoring rel!
		//    dAASSERT( joint != null && anum >= 0 && anum <= 2 && rel >= 0 && rel <= 2 );
		dAASSERT( anum >= 0 && anum <= 2 && rel >= 0 && rel <= 2 );
		//    checktype( joint, dxJointLimitMotor.class );

		if ( anum < 0 ) anum = 0;
		if ( anum > 2 ) anum = 2;

		//    if ( !joint.node[1].body && rel == 2 ) rel = 1; //ref 1
		if ( node[1].body == null && rel == 2 ) rel = 1; //ref 1

		_rel[anum] = rel;

//		dVector3 r = new dVector3(x, y, z, 0);
		//    r.v[0] = x;
		//    r.v[1] = y;
		//    r.v[2] = z;
		//    r.v[3] = 0;
		if ( rel > 0 )
		{
			if ( rel == 1 )
			{
				dMULTIPLY1_331( axis[anum], node[0].body._posr.R, r );
			}
			else
			{
				//second body has to exists thanks to ref 1 line
				dMULTIPLY1_331( axis[anum], node[1].body._posr.R, r );
			}
		}
		else
		{
			axis[anum].set(r);
			//        joint.axis[anum][0] = r[0];
			//        joint.axis[anum][1] = r[1];
			//        joint.axis[anum][2] = r[2];
		}

		dNormalize3( axis[anum] );
	}

	//void dJointSetLMotorNumAxes( dJoint j, int num )
	public void dJointSetLMotorNumAxes( int num )
	{
		if ( num < 0 ) num = 0;
		if ( num > 3 ) num = 3;
		this.num = num;
	}

	//void dJointSetLMotorParam( dJoint j, int parameter, dReal value )
	public void dJointSetLMotorParam( D_PARAM_NAMES_N parameter, double value )
	{
		int anum = parameter.toGROUP().getIndex();//val() >> 8;  //TODO use >>> ????
		if ( anum < 0 ) anum = 0;
		if ( anum > 2 ) anum = 2;
		//parameter &= 0xff;
		limot[anum].set( parameter.toSUB(), value );
	}

	//int dJointGetLMotorNumAxes( dJoint j )
	int dJointGetLMotorNumAxes( )
	{
//		dxJointLMotor joint = ( dxJointLMotor )j;
//		dAASSERT( joint );
//		checktype( joint, dxJointLimitMotor.class );
		return num;
	}


	//void dJointGetLMotorAxis( dJoint j, int anum, dVector3 result )
	void dJointGetLMotorAxis( int anum, DVector3 result )
	{
		//dxJointLMotor joint = ( dxJointLMotor )j;
		//dAASSERT( joint != null && anum >= 0 && anum < 3 );
		dAASSERT( anum >= 0 && anum < 3 );
		//checktype( joint, dxJointLimitMotor.class );
		if ( anum < 0 ) anum = 0;
		if ( anum > 2 ) anum = 2;
		//    result[0] = joint.axis[anum][0];
		//    result[1] = joint.axis[anum][1];
		//    result[2] = joint.axis[anum][2];
		result.set(axis[anum]);
	}

	//dReal dJointGetLMotorParam( dJoint j, int parameter )
	public double dJointGetLMotorParam( D_PARAM_NAMES_N parameter )
	{
		//    dxJointLMotor joint = ( dxJointLMotor )j;
		//    dAASSERT( joint );
		//    checktype( joint, dxJointLimitMotor.class );
		int anum = parameter.toGROUP().getIndex();//val() >> 8;
		if ( anum < 0 ) anum = 0;
		if ( anum > 2 ) anum = 2;
		//parameter &= 0xff;
		return limot[anum].get( parameter.toSUB() );
	}


	// ******************************
	// API dLMotorJoint
	// ******************************

	public void setNumAxes (int num)
	{ dJointSetLMotorNumAxes (num); }
	public int getNumAxes()
	{ return dJointGetLMotorNumAxes (); }

	public void setAxis (int anum, int rel, double x, double y, double z)
	{ dJointSetLMotorAxis (anum, rel, x, y, z); }
	public void setAxis (int anum, int rel, DVector3C a)
	{ dJointSetLMotorAxis (anum, rel, a); }
	public void getAxis (int anum, DVector3 result)
	{ dJointGetLMotorAxis (anum, result); }

	public void setParam (D_PARAM_NAMES_N parameter, double value)
	{ dJointSetLMotorParam (parameter, value); }
	public double getParam (D_PARAM_NAMES_N parameter)
	{ return dJointGetLMotorParam (parameter); }
}
