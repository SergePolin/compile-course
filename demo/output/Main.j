.class public Main
.super java/lang/Object

.field private static numbers [I

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 2
    iconst_5
    newarray int
    putstatic Main/numbers [I
    getstatic Main/numbers [I
    iconst_1
    iconst_1
    isub
    bipush 10
    iastore
    getstatic Main/numbers [I
    iconst_2
    iconst_1
    isub
    bipush 20
    iastore
    getstatic Main/numbers [I
    iconst_3
    iconst_1
    isub
    bipush 30
    iastore
    getstatic Main/numbers [I
    iconst_4
    iconst_1
    isub
    bipush 40
    iastore
    getstatic Main/numbers [I
    iconst_5
    iconst_1
    isub
    bipush 50
    iastore
    iconst_1
    istore 1
L0:
    iload 1
    iconst_5
    if_icmpgt L1
    getstatic java/lang/System/out Ljava/io/PrintStream;
    getstatic Main/numbers [I
    iload 1
    iconst_1
    isub
    iaload
    invokevirtual java/io/PrintStream/println(I)V

    iinc 1 1
    goto L0
L1:
    return
.end method
