.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 11

    ; var integerVar: integer is 10
    ldc 10
    istore_1

    ; var realVar: real is 3.14
    ldc 3.14
    fstore_2

    ; var boolVar: boolean is true
    iconst_1
    istore_3

    ; var intToReal: real is integerVar as real
    iload_1
    i2f
    fstore 4

    ; var realToInt: integer is realVar as integer
    fload_2
    f2i
    istore 5

    ; var boolToInt: integer is boolVar as integer
    iload_3
    istore 6

    ; var intToBool: boolean is integerVar as boolean
    iload_1
    iconst_0
    if_icmpeq False1
    iconst_1
    goto Store1
    False1:
    iconst_0
    Store1:
    istore 7

    ; var realToBool: boolean is realVar as boolean
    fload_2
    fconst_0
    fcmpl
    ifeq False2
    iconst_1
    goto Store2
    False2:
    iconst_0
    Store2:
    istore 8

    ; var boolToInt2: integer is boolVar as integer
    iload_3
    istore 9

    ; print statements
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_1
    invokevirtual java/io/PrintStream/println(I)V

    getstatic java/lang/System/out Ljava/io/PrintStream;
    fload_2
    invokevirtual java/io/PrintStream/println(F)V

    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_3
    invokevirtual java/io/PrintStream/println(Z)V

    return
.end method
