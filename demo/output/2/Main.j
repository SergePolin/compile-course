.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 3
    .limit locals 7
    
    ; var a: integer is 15
    ldc 15
    istore_1
    
    ; var b: integer is 5  
    ldc 5
    istore_2
    
    ; var sum: integer is a + b
    iload_1
    iload_2
    iadd
    istore_3
    
    ; var difference: integer is a - b
    iload_1
    iload_2
    isub
    istore 4
    
    ; var product: integer is a * b
    iload_1
    iload_2
    imul
    istore 5
    
    ; var quotient: integer is a / b
    iload_1
    iload_2
    idiv
    istore 6
    
    ; print statements
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_3
    invokevirtual java/io/PrintStream/println(I)V
    
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 4
    invokevirtual java/io/PrintStream/println(I)V
    
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 6
    invokevirtual java/io/PrintStream/println(I)V
    
    return
.end method
