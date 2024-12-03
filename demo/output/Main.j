.class public Main
.super java/lang/Object

.field private static scanner Ljava/util/Scanner;

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main()V
    .limit stack 20
    .limit locals 5

    iconst_0
    istore 0
    iconst_5
    istore 1
L0:
    iload 1
    iconst_1
    if_icmplt L1
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 1
    invokevirtual java/io/PrintStream/println(I)V

    iinc 1 -1
    goto L0
L1:
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 6
    .limit locals 20

    iconst_0
    istore 1
    iconst_5
    istore 2
L2:
    iload 2
    iconst_1
    if_icmplt L3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 2
    invokevirtual java/io/PrintStream/println(I)V

    iinc 2 -1
    goto L2
L3:

    return
.end method
