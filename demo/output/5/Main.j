.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 2
    .limit locals 2

    ; Initialize i to 1
    iconst_1
    istore_1

    ; While loop start
    Loop_Start:
    iload_1
    bipush 10
    if_icmpgt Loop_End

    ; Print i
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_1
    invokevirtual java/io/PrintStream/println(I)V

    ; i := i + 1
    iload_1
    iconst_1
    iadd
    istore_1

    goto Loop_Start
    Loop_End:

    return
.end method
