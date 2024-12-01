.class public Main
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 4
    .limit locals 5
    
    ; Create scanner for reading input
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    astore 4
    
    ; Read first integer (a)
    aload 4
    invokevirtual java/util/Scanner/nextInt()I
    istore_1
    
    ; Read second integer (b) 
    aload 4
    invokevirtual java/util/Scanner/nextInt()I
    istore_2
    
    ; Read operation string (op)
    aload 4
    invokevirtual java/util/Scanner/next()Ljava/lang/String;
    astore_3
    
    ; Compare operation and perform calculation
    aload_3
    ldc "+"
    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z
    ifeq L1
    iload_1
    iload_2
    iadd
    goto L5
L1:
    aload_3
    ldc "-" 
    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z
    ifeq L2
    iload_1
    iload_2
    isub
    goto L5
L2:
    aload_3
    ldc "*"
    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z
    ifeq L3
    iload_1
    iload_2
    imul
    goto L5
L3:
    aload_3
    ldc "/"
    invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z
    ifeq L4
    iload_1
    iload_2
    idiv
    goto L5
L4:
    iconst_0
L5:
    ; Store result
    istore 4
    
    ; Print result
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 4
    invokevirtual java/io/PrintStream/println(I)V
    
    return
.end method
