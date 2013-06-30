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
package org.ode4j.ode;

public interface DHashSpace extends DSpace {
	
	int getLevelMin ();
	int getLevelMax ();
	void setLevels(int minlevel, int maxlevel);

//	// intentionally undefined, don't use these
//	dHashSpace (dHashSpace &);
//	void operator= (dHashSpace &);
//
//	public:
//		dHashSpace (dSpace space)
//		{ _id = (dGeom) dHashSpaceCreate (space); }
//	void setLevels (int minlevel, int maxlevel)
//	{ dHashSpaceSetLevels (id(),minlevel,maxlevel); }
}