.class public Main
.super java/lang/Object

.method public <init>()V
    .limit stack 1
    .limit locals 1
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 5

    ; i = 1
    iconst_1
    istore_1

L1:
    iload_1
    iconst_5
    if_icmpgt L2

    ; j = 1
    iconst_1
    istore_2

L3:
    iload_2
    iconst_5
    if_icmpgt L4

    ; product = i * j
    iload_1
    iload_2
    imul
    istore_3

    ; Build and print the string "i * j = product"
    new java/lang/StringBuilder
    dup
    invokespecial java/lang/StringBuilder/<init>()V
    iload_1
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " * "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload_2
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    ldc " = "
    invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    iload_3
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    astore 4

    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    ; j++
    iinc 2 1
    goto L3

L4:
    ; i++
    iinc 1 1
    goto L1

L2:
    return
.end method
