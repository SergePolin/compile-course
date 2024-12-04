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
    iconst_0
    istore 1
    iconst_1
    istore 2
L0:
    iload 2
    iconst_5
    if_icmpgt L1
    iconst_1
    istore 3
L2:
    iload 3
    iconst_5
    if_icmpgt L3
    iconst_0
    istore 4
    ; var product: SimpleType(integer)
    iload 2
    iload 3
    imul
    istore 4

    getstatic java/lang/System/out Ljava/io/PrintStream;
    new java/lang/StringBuilder
    dup
    invokespecial java/lang/StringBuilder/<init>()V
    iload 2
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " * "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload 3
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " = "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload 4
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    iinc 3 1
    goto L2
L3:
    iinc 2 1
    goto L0
L1:
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 6
    .limit locals 20

    iconst_0
    istore 1
    iconst_0
    istore 2
    iconst_1
    istore 3
L4:
    iload 3
    iconst_5
    if_icmpgt L5
    iconst_1
    istore 4
L6:
    iload 4
    iconst_5
    if_icmpgt L7
    iconst_0
    istore 5
    ; var product: SimpleType(integer)
    iload 3
    iload 4
    imul
    istore 5

    getstatic java/lang/System/out Ljava/io/PrintStream;
    new java/lang/StringBuilder
    dup
    invokespecial java/lang/StringBuilder/<init>()V
    iload 3
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " * "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload 4
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " = "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload 5
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    iinc 4 1
    goto L6
L7:
    iinc 3 1
    goto L4
L5:

    return
.end method
