/*
 * This file is part of the Bridge Construction Kit distribution (https://github.com/https://github.com/nurbal/bck).
 * Copyright (c) 2019 Bruno Carrez.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * A fixed-point vector class
 *
 */

#include <jni.h>
#include <math.h>
#include "ZColor.h"
#include "helpers.h"

static ZColor constR(1,0,0,0);
static ZColor constG(0,1,0,0);
static ZColor constB(0,0,1,0);
static ZColor constA(0,0,0,1);
static ZColor constWHITE(1,1,1,1);
static ZColor constBLACK(0,0,0,1);
static ZColor constTRANSPARENT(0,0,0,1);
const ZColor* ZColor::R() {return &constR;}
const ZColor* ZColor::G() {return &constG;}
const ZColor* ZColor::B() {return &constB;}
const ZColor* ZColor::A() {return &constA;}
const ZColor* ZColor::WHITE() {return &constWHITE;}
const ZColor* ZColor::BLACK() {return &constBLACK;}
const ZColor* ZColor::TRANSPARENT() {return &constTRANSPARENT;}


ZColor::ZColor() : ZSmartObject()
{
	SetWhite();
}
ZColor::ZColor(float R,float G,float B,float A)
{
	r=R;
	g=G;
	b=B;
	a=A;
}

ZColor::~ZColor()
{

}


// setters
void ZColor::set(float R,float G,float B,float A)
{
	r=R;
	g=G;
	b=B;
	a=A;
}

void ZColor::copy(const ZColor* other)
{
	r = other->r;
	g = other->g;
	b = other->b;
	a = other->a;
}

bool ZColor::isEqual(const ZColor* other) const
{
	return (r==other->r && g==other->g && b==other->b && a==other->a);
}

// basic vector operations
void ZColor::add(const ZColor* other)
{
	r += other->r;
	g += other->g;
	b += other->b;
	a += other->a;
}

void ZColor::add(const ZColor* c1,const ZColor* c2)
{
	r = c1->r + c2->r;
	g = c1->g + c2->g;
	b = c1->b + c2->b;
	a = c1->a + c2->a;
}

void ZColor::sub(const ZColor* other)
{
	r -= other->r;
	g -= other->g;
	b -= other->b;
	a -= other->a;
}

void ZColor::sub(const ZColor* c1,const ZColor* c2)
{
	r = c1->r - c2->r;
	g = c1->g - c2->g;
	b = c1->b - c2->b;
	a = c1->a - c2->a;
}

void ZColor::mul(float f)
{
	r *= r;
	g *= g;
	b *= b;
	//a *= a;
}
void ZColor::mul(const ZColor* c)
{
	r *= c->r;
	g *= c->g;
	b *= c->b;
	a *= c->a;
}
void ZColor::mul(const ZColor* c1, const ZColor* c2)
{
	r = c1->r*c2->r;
	g = c1->g*c2->g;
	b = c1->b*c2->b;
	a = c1->a*c2->a;
}

void ZColor::mul(const ZColor* c, float f)
{
	r = c->r*f;
	g = c->g*f;
	b = c->b*f;
	a = c->a;//*f;
}
void ZColor::addMul(const ZColor* c, float f)
{
	r += c->r*f;
	g += c->g*f;
	b += c->b*f;
	//a += c->a*f;
}
void ZColor::mulAddMul(const ZColor* c1, float f1, const ZColor* c2, float f2)
{
	r = c1->r*f1 + c2->r*f2;
	g = c1->g*f1 + c2->r*f2;
	b = c1->b*f1 + c2->r*f2;
	a = c1->a*f1 + c2->r*f2;
}

void ZColor::clamp()
{
	if (r<0.f) r=0.f; else if (r>1.f) r=1.f;
	if (g<0.f) g=0.f; else if (g>1.f) g=1.f;
	if (b<0.f) b=0.f; else if (b>1.f) b=1.f;
	if (a<0.f) a=0.f; else if (a>1.f) a=1.f;
}

void ZColor::SetBlack()
{
	set(0,0,0,1);
}

void ZColor::SetWhite()
{
	set(1,1,1,1);
}


void ZColor::interpolateSoft(const ZColor* start, const ZColor* end, float coef)
{
	coef = 0.5f*(1.f-cosf(coef*3.1415926f));
	interpolateLinear(start,end,coef);
}

void ZColor::interpolateLinear(const ZColor* start, const ZColor* end, float coef)
{
	mulAddMul(start,1.f-coef,end,coef);
}

