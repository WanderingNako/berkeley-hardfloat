
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

package hardfloat.test

import hardfloat._
import chisel3._
import chisel3.util._

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class ValExec_AddFN(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val addFN = Module(new AddFN(expWidth, sigWidth))
    addFN.io.subOp := false.B
    addFN.io.a := io.a
    addFN.io.b := io.b
    addFN.io.roundingMode   := io.roundingMode
    addFN.io.detectTininess := io.detectTininess

    io.expected.recOut := io.expected.out

    io.actual.out := addFN.io.out
    io.actual.exceptionFlags := addFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class ValExec_MulFN(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val mulFN = Module(new MulFN(expWidth, sigWidth))
    mulFN.io.a := io.a
    mulFN.io.b := io.b
    mulFN.io.roundingMode   := io.roundingMode
    mulFN.io.detectTininess := io.detectTininess

    io.expected.recOut := io.expected.out

    io.actual.out := mulFN.io.out
    io.actual.exceptionFlags := mulFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class ValExec_MulAddFN(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))
        val c = Input(Bits((expWidth + sigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val mulAddFN = Module(new MulAddFN(expWidth, sigWidth))
    mulAddFN.io.op := 0.U
    mulAddFN.io.a := io.a
    mulAddFN.io.b := io.b
    mulAddFN.io.c := io.c
    mulAddFN.io.roundingMode   := io.roundingMode
    mulAddFN.io.detectTininess := io.detectTininess

    io.expected.recOut := io.expected.out

    io.actual.out := mulAddFN.io.out
    io.actual.exceptionFlags := mulAddFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class ValExec_CompareFN_lt(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))

        val expected = new Bundle {
            val out = Input(Bits(1.W))
            val exceptionFlags = Input(Bits(5.W))
        }
        val actual = new Bundle {
            val out = Output(Bits(1.W))
            val exceptionFlags = Output(Bits(5.W))
        }
        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val compareFN = Module(new CompareFN(expWidth, sigWidth))
    compareFN.io.a := io.a
    compareFN.io.b := io.b
    compareFN.io.signaling := true.B

    io.actual.out := compareFN.io.lt
    io.actual.exceptionFlags := compareFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        (io.actual.out === io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class ValExec_CompareFN_le(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))

        val expected = new Bundle {
            val out = Input(Bits(1.W))
            val exceptionFlags = Input(Bits(5.W))
        }
        val actual = new Bundle {
            val out = Output(Bits(1.W))
            val exceptionFlags = Output(Bits(5.W))
        }
        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val compareFN = Module(new CompareFN(expWidth, sigWidth))
    compareFN.io.a := io.a
    compareFN.io.b := io.b
    compareFN.io.signaling := true.B

    io.actual.out := compareFN.io.lt || compareFN.io.eq
    io.actual.exceptionFlags := compareFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        (io.actual.out === io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class ValExec_CompareFN_eq(expWidth: Int, sigWidth: Int) extends Module
{
    val io = IO(new Bundle {
        val a = Input(Bits((expWidth + sigWidth).W))
        val b = Input(Bits((expWidth + sigWidth).W))

        val expected = new Bundle {
            val out = Input(Bits(1.W))
            val exceptionFlags = Input(Bits(5.W))
        }
        val actual = new Bundle {
            val out = Output(Bits(1.W))
            val exceptionFlags = Output(Bits(5.W))
        }
        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val compareFN = Module(new CompareFN(expWidth, sigWidth))
    compareFN.io.a := io.a
    compareFN.io.b := io.b
    compareFN.io.signaling := false.B

    io.actual.out := compareFN.io.eq
    io.actual.exceptionFlags := compareFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        (io.actual.out === io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class
    ValExec_INToFN(intWidth: Int, expWidth: Int, sigWidth: Int)
    extends Module
{
    val io = IO(new Bundle {
        val in = Input(Bits(intWidth.W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val iNToFN = Module(new INToFN(intWidth, expWidth, sigWidth))
    iNToFN.io.signedIn := true.B
    iNToFN.io.in := io.in
    iNToFN.io.roundingMode   := io.roundingMode
    iNToFN.io.detectTininess := io.detectTininess

    io.expected.recOut := io.expected.out

    io.actual.out := iNToFN.io.out
    io.actual.exceptionFlags := iNToFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class
    ValExec_FNToIN(expWidth: Int, sigWidth: Int, intWidth: Int)
    extends Module
{
    val io = IO(new Bundle {
        val in = Input(Bits((expWidth + sigWidth).W))
        val roundingMode = Input(UInt(3.W))

        val expected = new Bundle {
            val out = Input(Bits(intWidth.W))
            val exceptionFlags = Input(Bits(5.W))
        }

        val actual = new Bundle {
            val out = Output(Bits(intWidth.W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val fNToIN = Module(new FNToIN(expWidth, sigWidth, intWidth))
    fNToIN.io.in := io.in
    fNToIN.io.roundingMode := io.roundingMode
    fNToIN.io.signedOut := true.B

    io.actual.out := fNToIN.io.out
    io.actual.exceptionFlags :=
        fNToIN.io.intExceptionFlags(2, 1).orR ##
            0.U(3.W) ##
            fNToIN.io.intExceptionFlags(0)

    io.check := true.B
    io.pass :=
        (io.actual.out === io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class
    ValExec_FNToFN(
        inExpWidth: Int, inSigWidth: Int, outExpWidth: Int, outSigWidth: Int)
    extends Module
{
    val io = IO(new Bundle {
        val in = Input(Bits((inExpWidth + inSigWidth).W))
        val roundingMode   = Input(UInt(3.W))
        val detectTininess = Input(UInt(1.W))

        val expected = new Bundle {
            val out = Input(Bits((outExpWidth + outSigWidth).W))
            val exceptionFlags = Input(Bits(5.W))
            val recOut = Output(Bits((outExpWidth + outSigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((outExpWidth + outSigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val fNToFN =
        Module(
            new FNToFN(inExpWidth, inSigWidth, outExpWidth, outSigWidth))
    fNToFN.io.in := io.in
    fNToFN.io.roundingMode   := io.roundingMode
    fNToFN.io.detectTininess := io.detectTininess

    io.expected.recOut := io.expected.out

    io.actual.out := fNToFN.io.out
    io.actual.exceptionFlags := fNToFN.io.exceptionFlags

    io.check := true.B
    io.pass :=
        equivFN(
            outExpWidth, outSigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class
    ValExec_DivSqrtFN_small_div(expWidth: Int, sigWidth: Int, options: Int)
    extends Module
{
    val io = IO(new Bundle {
        val input = Flipped(Decoupled(new DivRecFN_io(expWidth, sigWidth)))

        val expected = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val ds = Module(new DivSqrtFN_small(expWidth, sigWidth, options))
    val cq = Module(new Queue(new DivRecFN_io(expWidth, sigWidth), 5))

    cq.io.enq.valid := io.input.valid && ds.io.inReady
    cq.io.enq.bits := io.input.bits

    io.input.ready := ds.io.inReady && cq.io.enq.ready
    ds.io.inValid := io.input.valid && cq.io.enq.ready
    ds.io.sqrtOp := false.B
    ds.io.a := io.input.bits.a
    ds.io.b := io.input.bits.b
    ds.io.roundingMode   := io.input.bits.roundingMode
    ds.io.detectTininess := io.input.bits.detectTininess

    io.expected.out := cq.io.deq.bits.out
    io.expected.exceptionFlags := cq.io.deq.bits.exceptionFlags
    io.expected.recOut := cq.io.deq.bits.out

    io.actual.out := ds.io.out
    io.actual.exceptionFlags := ds.io.exceptionFlags

    cq.io.deq.ready := ds.io.outValid_div

    io.check := ds.io.outValid_div
    io.pass :=
        cq.io.deq.valid &&
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class
    ValExec_DivSqrtFN_small_sqrt(expWidth: Int, sigWidth: Int, options: Int)
    extends Module
{
    val io = IO(new Bundle {
        val input = Flipped(Decoupled(new SqrtRecFN_io(expWidth, sigWidth)))

        val expected = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
            val recOut = Output(Bits((expWidth + sigWidth).W))
        }

        val actual = new Bundle {
            val out = Output(Bits((expWidth + sigWidth).W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val ds = Module(new DivSqrtFN_small(expWidth, sigWidth, options))
    val cq = Module(new Queue(new SqrtRecFN_io(expWidth, sigWidth), 5))

    cq.io.enq.valid := io.input.valid && ds.io.inReady
    cq.io.enq.bits := io.input.bits

    io.input.ready := ds.io.inReady && cq.io.enq.ready
    ds.io.inValid := io.input.valid && cq.io.enq.ready
    ds.io.sqrtOp := true.B
    ds.io.a := io.input.bits.a
    ds.io.b := DontCare
    ds.io.roundingMode   := io.input.bits.roundingMode
    ds.io.detectTininess := io.input.bits.detectTininess

    io.expected.out := cq.io.deq.bits.out
    io.expected.exceptionFlags := cq.io.deq.bits.exceptionFlags
    io.expected.recOut := cq.io.deq.bits.out

    io.actual.out := ds.io.out
    io.actual.exceptionFlags := ds.io.exceptionFlags

    cq.io.deq.ready := ds.io.outValid_sqrt

    io.check := ds.io.outValid_sqrt
    io.pass :=
        cq.io.deq.valid &&
        equivFN(expWidth, sigWidth, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

//----------------------------------------------------------------------------
//----------------------------------------------------------------------------

class ValExec_DivSqrtF64_div extends Module
{
    val io = IO(new Bundle {
        val input = Flipped(Decoupled(new DivRecF64_io))

        val expected = new Bundle {
            val out = Output(Bits(64.W))
            val exceptionFlags = Output(Bits(5.W))
            val recOut = Output(Bits(64.W))
        }

        val actual = new Bundle {
            val out = Output(Bits(64.W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val ds = Module(new DivSqrtF64)
    val cq = Module(new Queue(new DivRecF64_io, 5))

    cq.io.enq.valid := io.input.valid && ds.io.inReady_div
    cq.io.enq.bits := io.input.bits

    io.input.ready := ds.io.inReady_div && cq.io.enq.ready
    ds.io.inValid := io.input.valid && cq.io.enq.ready
    ds.io.sqrtOp := false.B
    ds.io.a := io.input.bits.a
    ds.io.b := io.input.bits.b
    ds.io.roundingMode   := io.input.bits.roundingMode
    ds.io.detectTininess := io.input.bits.detectTininess

    io.expected.out := cq.io.deq.bits.out
    io.expected.exceptionFlags := cq.io.deq.bits.exceptionFlags
    io.expected.recOut := cq.io.deq.bits.out

    io.actual.out := ds.io.out
    io.actual.exceptionFlags := ds.io.exceptionFlags

    cq.io.deq.ready := ds.io.outValid_div

    io.check := ds.io.outValid_div
    io.pass :=
        cq.io.deq.valid &&
        equivFN(11, 53, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}

class ValExec_DivSqrtF64_sqrt extends Module
{
    val io = IO(new Bundle {
        val input = Flipped(Decoupled(new SqrtRecF64_io))

        val expected = new Bundle {
            val out = Output(Bits(64.W))
            val exceptionFlags = Output(Bits(5.W))
            val recOut = Output(Bits(64.W))
        }

        val actual = new Bundle {
            val out = Output(Bits(64.W))
            val exceptionFlags = Output(Bits(5.W))
        }

        val check = Output(Bool())
        val pass = Output(Bool())
    })

    val ds = Module(new DivSqrtF64)
    val cq = Module(new Queue(new SqrtRecF64_io, 5))

    cq.io.enq.valid := io.input.valid && ds.io.inReady_sqrt
    cq.io.enq.bits := io.input.bits

    io.input.ready := ds.io.inReady_sqrt && cq.io.enq.ready
    ds.io.inValid := io.input.valid && cq.io.enq.ready
    ds.io.sqrtOp := true.B
    ds.io.a := DontCare
    ds.io.b := io.input.bits.b
    ds.io.roundingMode   := io.input.bits.roundingMode
    ds.io.detectTininess := io.input.bits.detectTininess

    io.expected.out := cq.io.deq.bits.out
    io.expected.exceptionFlags := cq.io.deq.bits.exceptionFlags
    io.expected.recOut := cq.io.deq.bits.out

    io.actual.out := ds.io.out
    io.actual.exceptionFlags := ds.io.exceptionFlags

    cq.io.deq.ready := ds.io.outValid_sqrt

    io.check := ds.io.outValid_sqrt
    io.pass :=
        cq.io.deq.valid &&
        equivFN(11, 53, io.actual.out, io.expected.out) &&
        (io.actual.exceptionFlags === io.expected.exceptionFlags)
}
