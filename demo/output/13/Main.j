.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 3
    
    ; Create scanner for reading input
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    
    ; Read integer into num
    invokevirtual java/util/Scanner/nextInt()I
    istore_1
    
    ; Calculate square = num * num
    iload_1
    iload_1
    imul
    istore_2
    
    ; Print square
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_2
    invokevirtual java/io/PrintStream/println(I)V
    
    return
.end method
