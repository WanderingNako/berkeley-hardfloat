
/*============================================================================

This Chisel source file is part of a pre-release version of the HardFloat IEEE
Floating-Point Arithmetic Package, by John R. Hauser (with some contributions
from Yunsup Lee and Andrew Waterman, mainly concerning testing).

Copyright 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017 The Regents of the
University of California.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
    this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions, and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. Neither the name of the University nor the names of its contributors may be
    used to endorse or promote products derived from this software without
    specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS", AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, ARE
DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

=============================================================================*/

package hardfloat

import chisel3._

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------
// Wrappers that let each HardFloat unit operate directly on IEEE-format
// floating-point numbers ("fN").  Each wrapper converts its fN inputs with
// 'recFNFromFN', instantiates the original recoded-format unit unchanged,
// and converts the recoded result back with 'fNFromRecFN'.  Exception flags
// are format-independent and pass straight through.
//----------------------------------------------------------------------------

class AddFN(expWidth: Int, sigWidth: Int) extends RawModule
{
    val io = IO(new Bundle {
        val subOp = Input(Bool())
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val roundingMode = Input(UInt(3.W))
        val detectTininess = Input(Bool())
        val out = Output(Bits((expWidth + sigWidth).W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val addRecFN = Module(new AddRecFN(expWidth, sigWidth))
    addRecFN.io.subOp        := io.subOp
    addRecFN.io.a            := recFNFromFN(expWidth, sigWidth, io.a)
    addRecFN.io.b            := recFNFromFN(expWidth, sigWidth, io.b)
    addRecFN.io.roundingMode := io.roundingMode
    addRecFN.io.detectTininess := io.detectTininess

    io.out            := fNFromRecFN(expWidth, sigWidth, addRecFN.io.out)
    io.exceptionFlags := addRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class MulFN(expWidth: Int, sigWidth: Int) extends RawModule
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val roundingMode = Input(UInt(3.W))
        val detectTininess = Input(Bool())
        val out = Output(Bits((expWidth + sigWidth).W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val mulRecFN = Module(new MulRecFN(expWidth, sigWidth))
    mulRecFN.io.a            := recFNFromFN(expWidth, sigWidth, io.a)
    mulRecFN.io.b            := recFNFromFN(expWidth, sigWidth, io.b)
    mulRecFN.io.roundingMode := io.roundingMode
    mulRecFN.io.detectTininess := io.detectTininess

    io.out            := fNFromRecFN(expWidth, sigWidth, mulRecFN.io.out)
    io.exceptionFlags := mulRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class MulAddFN(expWidth: Int, sigWidth: Int) extends RawModule
{
    val io = IO(new Bundle {
        val op = Input(Bits(2.W))
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val c = Input(Bits((expWidth + sigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))
        val out = Output(Bits((expWidth + sigWidth).W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val mulAddRecFN = Module(new MulAddRecFN(expWidth, sigWidth))
    mulAddRecFN.io.op            := io.op
    mulAddRecFN.io.a             := recFNFromFN(expWidth, sigWidth, io.a)
    mulAddRecFN.io.b             := recFNFromFN(expWidth, sigWidth, io.b)
    mulAddRecFN.io.c             := recFNFromFN(expWidth, sigWidth, io.c)
    mulAddRecFN.io.roundingMode  := io.roundingMode
    mulAddRecFN.io.detectTininess := io.detectTininess

    io.out            := fNFromRecFN(expWidth, sigWidth, mulAddRecFN.io.out)
    io.exceptionFlags := mulAddRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class CompareFN(expWidth: Int, sigWidth: Int) extends RawModule
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val signaling = Input(Bool())
        val lt = Output(Bool())
        val eq = Output(Bool())
        val gt = Output(Bool())
        val exceptionFlags = Output(Bits(5.W))
    })

    val compareRecFN = Module(new CompareRecFN(expWidth, sigWidth))
    compareRecFN.io.a         := recFNFromFN(expWidth, sigWidth, io.a)
    compareRecFN.io.b         := recFNFromFN(expWidth, sigWidth, io.b)
    compareRecFN.io.signaling := io.signaling

    io.lt             := compareRecFN.io.lt
    io.eq             := compareRecFN.io.eq
    io.gt             := compareRecFN.io.gt
    io.exceptionFlags := compareRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

object classifyFN
{
    def apply(expWidth: Int, sigWidth: Int, in: Bits) =
        classifyRecFN(expWidth, sigWidth, recFNFromFN(expWidth, sigWidth, in))
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class INToFN(intWidth: Int, expWidth: Int, sigWidth: Int) extends RawModule
{
    val io = IO(new Bundle {
        val signedIn = Input(Bool())
        val in = Input(Bits(intWidth.W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))
        val out = Output(Bits((expWidth + sigWidth).W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val inToRecFN = Module(new INToRecFN(intWidth, expWidth, sigWidth))
    inToRecFN.io.signedIn       := io.signedIn
    inToRecFN.io.in             := io.in
    inToRecFN.io.roundingMode   := io.roundingMode
    inToRecFN.io.detectTininess := io.detectTininess

    io.out            := fNFromRecFN(expWidth, sigWidth, inToRecFN.io.out)
    io.exceptionFlags := inToRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class FNToIN(expWidth: Int, sigWidth: Int, intWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val in = Input(Bits((expWidth + sigWidth).W))
        val roundingMode = Input(UInt(3.W))
        val signedOut = Input(Bool())
        val out = Output(Bits(intWidth.W))
        val intExceptionFlags = Output(Bits(3.W))
    })

    val recFNToIN = Module(new RecFNToIN(expWidth, sigWidth, intWidth))
    recFNToIN.io.in          := recFNFromFN(expWidth, sigWidth, io.in)
    recFNToIN.io.roundingMode := io.roundingMode
    recFNToIN.io.signedOut   := io.signedOut

    io.out              := recFNToIN.io.out
    io.intExceptionFlags := recFNToIN.io.intExceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class
    FNToFN(
        inExpWidth: Int, inSigWidth: Int, outExpWidth: Int, outSigWidth: Int)
    extends RawModule
{
    val io = IO(new Bundle {
        val in = Input(Bits((inExpWidth + inSigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))
        val out = Output(Bits((outExpWidth + outSigWidth).W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val recFNToRecFN =
        Module(new RecFNToRecFN(inExpWidth, inSigWidth, outExpWidth, outSigWidth))
    recFNToRecFN.io.in             := recFNFromFN(inExpWidth, inSigWidth, io.in)
    recFNToRecFN.io.roundingMode    := io.roundingMode
    recFNToRecFN.io.detectTininess  := io.detectTininess

    io.out            := fNFromRecFN(outExpWidth, outSigWidth, recFNToRecFN.io.out)
    io.exceptionFlags := recFNToRecFN.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class DivSqrtFN_small(expWidth: Int, sigWidth: Int, options: Int) extends Module
{
    val io = IO(new Bundle {
        val inReady        = Output(Bool())
        val inValid        = Input(Bool())
        val sqrtOp         = Input(Bool())
        val a              = Input(Bits((expWidth + sigWidth).W))
        val b              = Input(Bits((expWidth + sigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))
        val outValid_div   = Output(Bool())
        val outValid_sqrt  = Output(Bool())
        val out            = Output(Bits((expWidth + sigWidth).W))
        val exceptionFlags = Output(UInt(5.W))
    })

    val divSqrtRecFN_small =
        Module(new DivSqrtRecFN_small(expWidth, sigWidth, options))
    io.inReady                := divSqrtRecFN_small.io.inReady
    divSqrtRecFN_small.io.inValid        := io.inValid
    divSqrtRecFN_small.io.sqrtOp         := io.sqrtOp
    divSqrtRecFN_small.io.a              := recFNFromFN(expWidth, sigWidth, io.a)
    divSqrtRecFN_small.io.b              := recFNFromFN(expWidth, sigWidth, io.b)
    divSqrtRecFN_small.io.roundingMode   := io.roundingMode
    divSqrtRecFN_small.io.detectTininess := io.detectTininess

    io.outValid_div   := divSqrtRecFN_small.io.outValid_div
    io.outValid_sqrt  := divSqrtRecFN_small.io.outValid_sqrt
    io.out            := fNFromRecFN(expWidth, sigWidth, divSqrtRecFN_small.io.out)
    io.exceptionFlags := divSqrtRecFN_small.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class DivSqrtF64 extends Module
{
    val io = IO(new Bundle {
        val inReady_div  = Output(Bool())
        val inReady_sqrt = Output(Bool())
        val inValid = Input(Bool())
        val sqrtOp = Input(Bool())
        val a = Input(Bits(64.W))
        val b = Input(Bits(64.W))
        val roundingMode   = Input(Bits(3.W))
        val detectTininess = Input(UInt(1.W))
        val outValid_div  = Output(Bool())
        val outValid_sqrt = Output(Bool())
        val out = Output(Bits(64.W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val divSqrtRecF64 = Module(new DivSqrtRecF64)
    io.inReady_div    := divSqrtRecF64.io.inReady_div
    io.inReady_sqrt   := divSqrtRecF64.io.inReady_sqrt
    divSqrtRecF64.io.inValid        := io.inValid
    divSqrtRecF64.io.sqrtOp         := io.sqrtOp
    divSqrtRecF64.io.a              := recFNFromFN(11, 53, io.a)
    divSqrtRecF64.io.b              := recFNFromFN(11, 53, io.b)
    divSqrtRecF64.io.roundingMode   := io.roundingMode
    divSqrtRecF64.io.detectTininess := io.detectTininess

    io.outValid_div   := divSqrtRecF64.io.outValid_div
    io.outValid_sqrt  := divSqrtRecF64.io.outValid_sqrt
    io.out            := fNFromRecFN(11, 53, divSqrtRecF64.io.out)
    io.exceptionFlags := divSqrtRecF64.io.exceptionFlags
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class DivSqrtF64_mulAddZ31(options: Int) extends Module
{
    val io = IO(new Bundle {
        val inReady_div    = Output(Bool())
        val inReady_sqrt   = Output(Bool())
        val inValid        = Input(Bool())
        val sqrtOp         = Input(Bool())
        val a              = Input(Bits(64.W))
        val b              = Input(Bits(64.W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))
        val usingMulAdd    = Output(Bits(4.W))
        val latchMulAddA_0 = Output(Bool())
        val mulAddA_0      = Output(UInt(54.W))
        val latchMulAddB_0 = Output(Bool())
        val mulAddB_0      = Output(UInt(54.W))
        val mulAddC_2      = Output(UInt(105.W))
        val mulAddResult_3 = Input(UInt(105.W))
        val outValid_div   = Output(Bool())
        val outValid_sqrt  = Output(Bool())
        val out            = Output(Bits(64.W))
        val exceptionFlags = Output(Bits(5.W))
    })

    val divSqrtRecF64_mulAddZ31 = Module(new DivSqrtRecF64_mulAddZ31(options))
    io.inReady_div  := divSqrtRecF64_mulAddZ31.io.inReady_div
    io.inReady_sqrt := divSqrtRecF64_mulAddZ31.io.inReady_sqrt
    divSqrtRecF64_mulAddZ31.io.inValid        := io.inValid
    divSqrtRecF64_mulAddZ31.io.sqrtOp         := io.sqrtOp
    divSqrtRecF64_mulAddZ31.io.a              := recFNFromFN(11, 53, io.a)
    divSqrtRecF64_mulAddZ31.io.b              := recFNFromFN(11, 53, io.b)
    divSqrtRecF64_mulAddZ31.io.roundingMode   := io.roundingMode
    divSqrtRecF64_mulAddZ31.io.detectTininess := io.detectTininess

    io.usingMulAdd    := divSqrtRecF64_mulAddZ31.io.usingMulAdd
    io.latchMulAddA_0 := divSqrtRecF64_mulAddZ31.io.latchMulAddA_0
    io.mulAddA_0      := divSqrtRecF64_mulAddZ31.io.mulAddA_0
    io.latchMulAddB_0 := divSqrtRecF64_mulAddZ31.io.latchMulAddB_0
    io.mulAddB_0      := divSqrtRecF64_mulAddZ31.io.mulAddB_0
    io.mulAddC_2      := divSqrtRecF64_mulAddZ31.io.mulAddC_2
    divSqrtRecF64_mulAddZ31.io.mulAddResult_3 := io.mulAddResult_3

    io.outValid_div   := divSqrtRecF64_mulAddZ31.io.outValid_div
    io.outValid_sqrt  := divSqrtRecF64_mulAddZ31.io.outValid_sqrt
    io.out            := fNFromRecFN(11, 53, divSqrtRecF64_mulAddZ31.io.out)
    io.exceptionFlags := divSqrtRecF64_mulAddZ31.io.exceptionFlags
}
