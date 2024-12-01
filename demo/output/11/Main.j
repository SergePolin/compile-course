.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 2
    .limit locals 3

    ; Initialize variables
    iconst_1
    istore_1  ; a = true
    iconst_0  
    istore_2  ; b = false

    ; Print a and b
    iload_1
    iload_2
    iand
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(Z)V

    ; Print a or b
    iload_1
    iload_2
    ior
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(Z)V

    ; Print not a
    iload_1
    iconst_1
    ixor
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(Z)V

    ; Print a xor b
    iload_1
    iload_2
    ixor
    getstatic java/lang/System/out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream/println(Z)V

    return
.end method
