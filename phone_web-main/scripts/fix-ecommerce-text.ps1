$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
$base = 'C:\Users\thien lam\Desktop\phone_web-main\phone_web-main\ecommerce-web\src\main\resources\templates'

function Update-File {
    param(
        [string]$Name,
        [scriptblock]$Transform
    )

    $path = Join-Path $base $Name
    $content = [IO.File]::ReadAllText($path)
    $content = & $Transform $content
    [IO.File]::WriteAllText($path, $content, $utf8NoBom)
}

Update-File 'products.html' {
    param($c)
    $c = [regex]::Replace($c, '<title>.*?</title>', '<title>Phone Store | S&#7843;n ph&#7849;m</title>', 1)
    $c = [regex]::Replace($c, '<span>.*?mua nhanh.*?</span>', '<span>&#272;i&#7879;n tho&#7841;i ch&#237;nh h&#227;ng, gi&#225; r&#245; r&#224;ng, mua nhanh t&#7841;i nh&#224;</span>', 1)
    $c = $c.Replace('<span>Mua sáº¯m trá»±c tuyáº¿n</span>', '<span>Mua s&#7855;m tr&#7921;c tuy&#7871;n</span>')
    $c = $c.Replace('placeholder="TÃ¬m theo tÃªn sáº£n pháº©m, hÃ£ng, dÃ²ng mÃ¡y"', 'placeholder="T&#236;m theo t&#234;n s&#7843;n ph&#7849;m, h&#227;ng, d&#242;ng m&#225;y"')
    $c = $c.Replace('>TÃ¬m kiáº¿m<', '>T&#236;m ki&#7871;m<')
    $c = $c.Replace('>Táº¥t cáº£ sáº£n pháº©m<', '>T&#7845;t c&#7843; s&#7843;n ph&#7849;m<')
    $c = $c.Replace('>Tra cá»©u Ä‘Æ¡n<', '>Tra c&#7913;u &#273;&#417;n<')
    $c = $c.Replace('>Gian hÃ ng Ä‘iá»‡n thoáº¡i<', '>Gian h&#224;ng &#273;i&#7879;n tho&#7841;i<')
    $c = $c.Replace('Chá»n mÃ¡y phÃ¹ há»£p, xem giÃ¡ rÃµ rÃ ng vÃ  Ä‘áº·t mua tháº­t nhanh.', 'Ch&#7885;n m&#225;y ph&#249; h&#7907;p, xem gi&#225; r&#245; r&#224;ng v&#224; &#273;&#7863;t mua th&#7853;t nhanh.')
    $c = $c.Replace('Báº¡n cÃ³ thá»ƒ tÃ¬m theo tÃªn mÃ¡y, lá»c theo giÃ¡, sáº¯p xáº¿p láº¡i danh sÃ¡ch vÃ  vÃ o trang chi tiáº¿t Ä‘á»ƒ xem thÃªm.', 'B&#7841;n c&#243; th&#7875; t&#236;m theo t&#234;n m&#225;y, l&#7885;c theo gi&#225;, s&#7855;p x&#7871;p l&#7841;i danh s&#225;ch v&#224; v&#224;o trang chi ti&#7871;t &#273;&#7875; xem th&#234;m.')
    $c = $c.Replace('NhÃ³m ná»•i báº­t', 'Nh&#243;m n&#7893;i b&#7853;t')
    $c = $c.Replace('iPhone, Samsung vÃ  nhiá»u lá»±a chá»n khÃ¡c', 'iPhone, Samsung v&#224; nhi&#7873;u l&#7921;a ch&#7885;n kh&#225;c')
    $c = $c.Replace('DÃ¹ng thanh tÃ¬m kiáº¿m Ä‘á»ƒ rÃºt gá»n danh sÃ¡ch Ä‘Ãºng vá»›i mÃ¡y báº¡n Ä‘ang cáº§n.', 'D&#249;ng thanh t&#236;m ki&#7871;m &#273;&#7875; r&#250;t g&#7885;n danh s&#225;ch &#273;&#250;ng v&#7899;i m&#225;y b&#7841;n &#273;ang c&#7847;n.')
    $c = $c.Replace('Tra cá»©u dá»… dÃ ng', 'Tra c&#7913;u d&#7877; d&#224;ng')
    $c = $c.Replace('Xem láº¡i Ä‘Æ¡n theo sá»‘ Ä‘iá»‡n thoáº¡i', 'Xem l&#7841;i &#273;&#417;n theo s&#7889; &#273;i&#7879;n tho&#7841;i')
    $c = $c.Replace('Sau khi Ä‘áº·t mua, báº¡n chá»‰ cáº§n nháº­p sá»‘ Ä‘iá»‡n thoáº¡i Ä‘á»ƒ kiá»ƒm tra danh sÃ¡ch Ä‘Æ¡n vÃ  chi tiáº¿t tá»«ng Ä‘Æ¡n.', 'Sau khi &#273;&#7863;t mua, b&#7841;n ch&#7881; c&#7847;n nh&#7853;p s&#7889; &#273;i&#7879;n tho&#7841;i &#273;&#7875; ki&#7875;m tra danh s&#225;ch &#273;&#417;n v&#224; chi ti&#7871;t t&#7915;ng &#273;&#417;n.')
    $c = $c.Replace('Bá»™ lá»c', 'B&#7897; l&#7885;c')
    $c = $c.Replace('Lá»c sáº£n pháº©m', 'L&#7885;c s&#7843;n ph&#7849;m')
    $c = $c.Replace('Sáº¯p xáº¿p', 'S&#7855;p x&#7871;p')
    $c = $c.Replace('Máº·c Ä‘á»‹nh', 'M&#7863;c &#273;&#7883;nh')
    $c = $c.Replace('Má»›i cáº­p nháº­t', 'M&#7899;i c&#7853;p nh&#7853;t')
    $c = $c.Replace('ÄÆ°á»£c mua nhiá»u', '&#272;&#432;&#7907;c mua nhi&#7873;u')
    $c = $c.Replace('GiÃ¡ tháº¥p Ä‘áº¿n cao', 'Gi&#225; th&#7845;p &#273;&#7871;n cao')
    $c = $c.Replace('GiÃ¡ cao Ä‘áº¿n tháº¥p', 'Gi&#225; cao &#273;&#7871;n th&#7845;p')
    $c = $c.Replace('GiÃ¡ tá»«', 'Gi&#225; t&#7915;')
    $c = $c.Replace('GiÃ¡ Ä‘áº¿n', 'Gi&#225; &#273;&#7871;n')
    $c = $c.Replace('LÃ m má»›i', 'L&#224;m m&#7899;i')
    $c = $c.Replace('Ãp dá»¥ng', '&#193;p d&#7909;ng')
    $c = $c.Replace('Danh sÃ¡ch Ä‘ang cÃ³', 'Danh s&#225;ch &#273;ang c&#243;')
    $c = $c.Replace('phÃ¹ há»£p vá»›i lá»±a chá»n hiá»‡n táº¡i.', 'ph&#249; h&#7907;p v&#7899;i l&#7921;a ch&#7885;n hi&#7879;n t&#7841;i.')
    $c = $c.Replace('Äiá»‡n thoáº¡i', '&#272;i&#7879;n tho&#7841;i')
    $c = $c.Replace('Danh má»¥c', 'Danh m&#7909;c')
    $c = $c.Replace('TÃªn sáº£n pháº©m', 'T&#234;n s&#7843;n ph&#7849;m')
    $c = $c.Replace('Bá»™ nhá»› ', 'B&#7897; nh&#7899; ')
    $c = $c.Replace('Cáº¥u hÃ¬nh', 'C&#7845;u h&#236;nh')
    $c = $c.Replace('Xem chi tiáº¿t', 'Xem chi ti&#7871;t')
    $c = $c.Replace('Äáº·t mua ngay', '&#272;&#7863;t mua ngay')
    $c = $c.Replace('KhÃ´ng tÃ¬m tháº¥y sáº£n pháº©m phÃ¹ há»£p. Báº¡n hÃ£y thá»­ Ä‘á»•i tá»« khÃ³a, khoáº£ng giÃ¡ hoáº·c cÃ¡ch sáº¯p xáº¿p.', 'Kh&#244;ng t&#236;m th&#7845;y s&#7843;n ph&#7849;m ph&#249; h&#7907;p. B&#7841;n h&#227;y th&#7917; &#273;&#7893;i t&#7915; kh&#243;a, kho&#7843;ng gi&#225; ho&#7863;c c&#225;ch s&#7855;p x&#7871;p.')
    $c = $c.Replace('Trang bÃ¡n hÃ ng Ä‘iá»‡n thoáº¡i chÃ­nh hÃ£ng vá»›i luá»“ng Ä‘áº·t mua gá»n gÃ ng.', 'Trang b&#225;n h&#224;ng &#273;i&#7879;n tho&#7841;i ch&#237;nh h&#227;ng v&#7899;i lu&#7891;ng &#273;&#7863;t mua g&#7885;n g&#224;ng.')
    $c = $c.Replace('Danh sÃ¡ch rÃµ rÃ ng, bá»™ lá»c Ä‘Æ¡n giáº£n, tÃ¬m kiáº¿m trá»±c tiáº¿p theo tÃªn mÃ¡y.', 'Danh s&#225;ch r&#245; r&#224;ng, b&#7897; l&#7885;c &#273;&#417;n gi&#7843;n, t&#236;m ki&#7871;m tr&#7921;c ti&#7871;p theo t&#234;n m&#225;y.')
    $c = $c.Replace('Thanh toÃ¡n', 'Thanh to&#225;n')
    $c = $c.Replace('Äáº·t hÃ ng nhanh, hiá»ƒn thá»‹ tá»•ng tiá»n rÃµ rÃ ng trÆ°á»›c khi xÃ¡c nháº­n.', '&#272;&#7863;t h&#224;ng nhanh, hi&#7875;n th&#7883; t&#7893;ng ti&#7873;n r&#245; r&#224;ng tr&#432;&#7899;c khi x&#225;c nh&#7853;n.')
    $c = $c.Replace('Há»— trá»£', 'H&#7895; tr&#7907;')
    $c = $c.Replace('Bong bÃ³ng chat náº±m gá»n á»Ÿ gÃ³c pháº£i Ä‘á»ƒ há»i nhanh khi cáº§n.', 'Bong b&#243;ng chat n&#7857;m g&#7885;n &#7903; g&#243;c ph&#7843;i &#273;&#7875; h&#7887;i nhanh khi c&#7847;n.')
    $c = $c.Replace('TÆ° váº¥n sáº£n pháº©m', 'T&#432; v&#7845;n s&#7843;n ph&#7849;m')
    $c = $c.Replace('ChÃ o báº¡n, báº¡n cáº§n tÃ¬m sáº£n pháº©m nhÆ° tháº¿ nÃ o?', 'Ch&#224;o b&#7841;n, b&#7841;n c&#7847;n t&#236;m s&#7843;n ph&#7849;m nh&#432; th&#7871; n&#224;o?')
    $c = $c.Replace('KhÃ¡ch hÃ ng', 'Kh&#225;ch h&#224;ng')
    $c = $c.Replace('Nháº¯n Ä‘á»ƒ tÃ¬m sáº£n pháº©m phÃ¹ há»£p', 'Nh&#7855;n &#273;&#7875; t&#236;m s&#7843;n ph&#7849;m ph&#249; h&#7907;p')
    $c = $c.Replace('Gá»­i', 'G&#7917;i')
    return $c
}

Update-File 'product-detail.html' {
    param($c)
    $list = @(
        @('Chi tiáº¿t sáº£n pháº©m','Chi ti&#7871;t s&#7843;n ph&#7849;m'),
        @('Quay láº¡i danh sÃ¡ch','Quay l&#7841;i danh s&#225;ch'),
        @('ThÃ´ng tin sáº£n pháº©m','Th&#244;ng tin s&#7843;n ph&#7849;m'),
        @('Tra cá»©u Ä‘Æ¡n','Tra c&#7913;u &#273;&#417;n'),
        @('GiÃ¡ bÃ¡n','Gi&#225; b&#225;n'),
        @('Táº¥t cáº£ sáº£n pháº©m','T&#7845;t c&#7843; s&#7843;n ph&#7849;m'),
        @('Sáº£n pháº©m liÃªn quan','S&#7843;n ph&#7849;m li&#234;n quan'),
        @('Danh má»¥c','Danh m&#7909;c'),
        @('Äiá»‡n thoáº¡i','&#272;i&#7879;n tho&#7841;i'),
        @('TÃªn sáº£n pháº©m','T&#234;n s&#7843;n ph&#7849;m'),
        @('MÃ´ táº£ sáº£n pháº©m','M&#244; t&#7843; s&#7843;n ph&#7849;m'),
        @('MÃ n hÃ¬nh','M&#224;n h&#236;nh'),
        @('Sá»‘ lÆ°á»£ng','S&#7889; l&#432;&#7907;ng'),
        @('Quay láº¡i','Quay l&#7841;i'),
        @('Äáº¿n bÆ°á»›c Ä‘áº·t mua','&#272;&#7871;n b&#432;&#7899;c &#273;&#7863;t mua'),
        @('Gá»£i Ã½ thÃªm','G&#7907;i &#253; th&#234;m'),
        @('Sáº£n pháº©m cÃ¹ng nhÃ³m','S&#7843;n ph&#7849;m c&#249;ng nh&#243;m'),
        @('TÆ° váº¥n sáº£n pháº©m','T&#432; v&#7845;n s&#7843;n ph&#7849;m'),
        @('ChÃ o báº¡n, báº¡n cáº§n tÃ¬m sáº£n pháº©m nhÆ° tháº¿ nÃ o?','Ch&#224;o b&#7841;n, b&#7841;n c&#7847;n t&#236;m s&#7843;n ph&#7849;m nh&#432; th&#7871; n&#224;o?'),
        @('KhÃ¡ch hÃ ng','Kh&#225;ch h&#224;ng'),
        @('Nháº¯n Ä‘á»ƒ tÃ¬m sáº£n pháº©m phÃ¹ há»£p','Nh&#7855;n &#273;&#7875; t&#236;m s&#7843;n ph&#7849;m ph&#249; h&#7907;p'),
        @('Gá»­i','G&#7917;i')
    )
    foreach ($pair in $list) { $c = $c.Replace($pair[0], $pair[1]) }
    return $c
}

Update-File 'checkout.html' {
    param($c)
    $list = @(
        @('Phone Store | Äáº·t mua','Phone Store | &#272;&#7863;t mua'),
        @('HoÃ n táº¥t Ä‘áº·t mua','Ho&#224;n t&#7845;t &#273;&#7863;t mua'),
        @('Quay lai san pham','Quay l&#7841;i s&#7843;n ph&#7849;m'),
        @('Xac nhan don mua','X&#225;c nh&#7853;n &#273;&#417;n mua'),
        @('Xac nhan thong tin nhan hang','X&#225;c nh&#7853;n th&#244;ng tin nh&#7853;n h&#224;ng'),
        @('Tra c?u ??n','Tra c&#7913;u &#273;&#417;n'),
        @('Tong tien','T&#7893;ng ti&#7873;n'),
        @('San pham','S&#7843;n ph&#7849;m'),
        @('Chi tiet','Chi ti&#7871;t'),
        @('Thong tin nhan hang','Th&#244;ng tin nh&#7853;n h&#224;ng'),
        @('Ho?n t?t ??n mua','Ho&#224;n t&#7845;t &#273;&#417;n mua'),
        @('Ho va ten','H&#7885; v&#224; t&#234;n'),
        @('So dien thoai','S&#7889; &#273;i&#7879;n tho&#7841;i'),
        @('Dia chi nhan hang','&#272;&#7883;a ch&#7881; nh&#7853;n h&#224;ng'),
        @('Ghi chu','Ghi ch&#250;'),
        @('Vi du: goi truoc khi giao','V&#237; d&#7909;: g&#7885;i tr&#432;&#7899;c khi giao'),
        @('Quay lai','Quay l&#7841;i'),
        @('XÃ¡c nháº­n Ä‘áº·t mua','X&#225;c nh&#7853;n &#273;&#7863;t mua'),
        @('Thong tin don','Th&#244;ng tin &#273;&#417;n'),
        @('Danh muc','Danh m&#7909;c'),
        @('Bo nho ','B&#7897; nh&#7899; '),
        @('Thong so','Th&#244;ng s&#7889;'),
        @('So luong','S&#7889; l&#432;&#7907;ng'),
        @('Gia san pham','Gi&#225; s&#7843;n ph&#7849;m'),
        @('Tam tinh','T&#7841;m t&#237;nh'),
        @('Tong truoc giam','T&#7893;ng tr&#432;&#7899;c gi&#7843;m'),
        @('Tong tien','T&#7893;ng ti&#7873;n'),
        @('Äáº·t mua thÃ nh cÃ´ng','&#272;&#7863;t mua th&#224;nh c&#244;ng'),
        @('M? ??n','M&#227; &#273;&#417;n'),
        @('Giam gia','Gi&#7843;m gi&#225;'),
        @('Tiep tuc mua','Ti&#7871;p t&#7909;c mua'),
        @('Tu van san pham','T&#432; v&#7845;n s&#7843;n ph&#7849;m'),
        @('Chao ban, ban can tim san pham nhu the nao?','Ch&#224;o b&#7841;n, b&#7841;n c&#7847;n t&#236;m s&#7843;n ph&#7849;m nh&#432; th&#7871; n&#224;o?'),
        @('Khach hang','Kh&#225;ch h&#224;ng'),
        @('Nh?n ?? t?m s?n ph?m ph? h?p','Nh&#7855;n &#273;&#7875; t&#236;m s&#7843;n ph&#7849;m ph&#249; h&#7907;p'),
        @('Gui','G&#7917;i'),
        @('Nháº­p thÃ´ng tin ngÆ°á»i nháº­n vÃ  Ã¡p voucher trÆ°á»›c khi xÃ¡c nháº­n.','Nh&#7853;p th&#244;ng tin ng&#432;&#7901;i nh&#7853;n v&#224; &#225;p voucher tr&#432;&#7899;c khi x&#225;c nh&#7853;n.'),
        @('MÃ£ voucher','M&#227; voucher'),
        @('Nháº­p mÃ£ giáº£m giÃ¡','Nh&#7853;p m&#227; gi&#7843;m gi&#225;'),
        @('Ãp mÃ£','&#193;p m&#227;')
    )
    foreach ($pair in $list) { $c = $c.Replace($pair[0], $pair[1]) }
    return $c
}

Update-File 'tracking.html' {
    param($c)
    $list = @(
        @('Phone Store | Tra cá»©u Ä‘Æ¡n','Phone Store | Tra c&#7913;u &#273;&#417;n'),
        @('Tra cá»©u Ä‘Æ¡n mua','Tra c&#7913;u &#273;&#417;n mua'),
        @('Quay láº¡i gian hÃ ng','Quay l&#7841;i gian h&#224;ng'),
        @('Nháº­p sá»‘ Ä‘iá»‡n thoáº¡i Ä‘á»ƒ xem danh sÃ¡ch Ä‘Æ¡n mua','Nh&#7853;p s&#7889; &#273;i&#7879;n tho&#7841;i &#273;&#7875; xem danh s&#225;ch &#273;&#417;n mua'),
        @('Vá» trang sáº£n pháº©m','V&#7873; trang s&#7843;n ph&#7849;m'),
        @('Há»— trá»£','H&#7895; tr&#7907;'),
        @('DÃ¹ng Ä‘Ãºng sá»‘ Ä‘iá»‡n thoáº¡i khi Ä‘áº·t mua','D&#249;ng &#273;&#250;ng s&#7889; &#273;i&#7879;n tho&#7841;i khi &#273;&#7863;t mua'),
        @('Sáº£n pháº©m','S&#7843;n ph&#7849;m'),
        @('Tra cá»©u','Tra c&#7913;u'),
        @('Kiá»ƒm tra tÃ¬nh tráº¡ng Ä‘Æ¡n mua','Ki&#7875;m tra t&#236;nh tr&#7841;ng &#273;&#417;n mua'),
        @('Nháº­p sá»‘ Ä‘iá»‡n thoáº¡i Ä‘á»ƒ xem danh sÃ¡ch Ä‘Æ¡n, sau Ä‘Ã³ báº¥m vÃ o Ä‘Æ¡n trong danh sÃ¡ch Ä‘á»ƒ xem chi tiáº¿t.','Nh&#7853;p s&#7889; &#273;i&#7879;n tho&#7841;i &#273;&#7875; xem danh s&#225;ch &#273;&#417;n, sau &#273;&#243; b&#7845;m v&#224;o &#273;&#417;n trong danh s&#225;ch &#273;&#7875; xem chi ti&#7871;t.'),
        @('Sá»‘ Ä‘iá»‡n thoáº¡i','S&#7889; &#273;i&#7879;n tho&#7841;i'),
        @('Quay láº¡i','Quay l&#7841;i'),
        @('HÆ°á»›ng dáº«n','H&#432;&#7899;ng d&#7851;n'),
        @('CÃ¡ch xem Ä‘Æ¡n nhanh','C&#225;ch xem &#273;&#417;n nhanh'),
        @('Nháº­p sá»‘ Ä‘iá»‡n thoáº¡i Ä‘Ã£ dÃ¹ng khi Ä‘áº·t hÃ ng.','Nh&#7853;p s&#7889; &#273;i&#7879;n tho&#7841;i &#273;&#227; d&#249;ng khi &#273;&#7863;t h&#224;ng.'),
        @('Chá»n Ä‘Æ¡n muá»‘n xem trong danh sÃ¡ch.','Ch&#7885;n &#273;&#417;n mu&#7889;n xem trong danh s&#225;ch.'),
        @('Xem tráº¡ng thÃ¡i thanh toÃ¡n vÃ  giao hÃ ng.','Xem tr&#7841;ng th&#225;i thanh to&#225;n v&#224; giao h&#224;ng.'),
        @('Danh sÃ¡ch Ä‘Æ¡n','Danh s&#225;ch &#273;&#417;n'),
        @('ÄÆ¡n hÃ ng cá»§a báº¡n','&#272;&#417;n h&#224;ng c&#7911;a b&#7841;n'),
        @('ÄÆ¡n ngÃ y ','&#272;&#417;n ng&#224;y '),
        @('KhÃ¡ch hÃ ng','Kh&#225;ch h&#224;ng'),
        @('Nguá»“n: ','Ngu&#7891;n: '),
        @('NhÃ¢n viÃªn: ','Nh&#226;n vi&#234;n: '),
        @('Tráº¡ng thÃ¡i','Tr&#7841;ng th&#225;i'),
        @('Giáº£m: ','Gi&#7843;m: '),
        @('ChÆ°a cÃ³ dá»¯ liá»‡u Ä‘Æ¡n hÃ ng. Vui lÃ²ng nháº­p sá»‘ Ä‘iá»‡n thoáº¡i Ä‘á»ƒ tra cá»©u.','Ch&#432;a c&#243; d&#7919; li&#7879;u &#273;&#417;n h&#224;ng. Vui l&#242;ng nh&#7853;p s&#7889; &#273;i&#7879;n tho&#7841;i &#273;&#7875; tra c&#7913;u.'),
        @('Chi tiáº¿t','Chi ti&#7871;t'),
        @('Sáº£n pháº©m trong Ä‘Æ¡n','S&#7843;n ph&#7849;m trong &#273;&#417;n'),
        @('Voucher Ä‘Ã£ Ã¡p dá»¥ng: ','Voucher &#273;&#227; &#225;p d&#7909;ng: '),
        @('Tá»•ng tiá»n','T&#7893;ng ti&#7873;n'),
        @('Giáº£m Ä‘Æ¡n: ','Gi&#7843;m &#273;&#417;n: '),
        @('Sá»‘ lÆ°á»£ng: ','S&#7889; l&#432;&#7907;ng: '),
        @('ÄÆ¡n giÃ¡: ','&#272;&#417;n gi&#225;: '),
        @('ThÃ nh tiá»n','Th&#224;nh ti&#7873;n'),
        @('ChÆ°a chá»n Ä‘Æ¡n nÃ o. Báº¥m vÃ o Ä‘Æ¡n trong danh sÃ¡ch bÃªn trÃ¡i Ä‘á»ƒ xem chi tiáº¿t.','Ch&#432;a ch&#7885;n &#273;&#417;n n&#224;o. B&#7845;m v&#224;o &#273;&#417;n trong danh s&#225;ch b&#234;n tr&#225;i &#273;&#7875; xem chi ti&#7871;t.'),
        @('TÆ° váº¥n sáº£n pháº©m','T&#432; v&#7845;n s&#7843;n ph&#7849;m'),
        @('ChÃ o báº¡n, báº¡n cáº§n tÃ¬m sáº£n pháº©m nhÆ° tháº¿ nÃ o?','Ch&#224;o b&#7841;n, b&#7841;n c&#7847;n t&#236;m s&#7843;n ph&#7849;m nh&#432; th&#7871; n&#224;o?'),
        @('Nháº¯n Ä‘á»ƒ tÃ¬m sáº£n pháº©m phÃ¹ há»£p','Nh&#7855;n &#273;&#7875; t&#236;m s&#7843;n ph&#7849;m ph&#249; h&#7907;p'),
        @('Gá»­i','G&#7917;i')
    )
    foreach ($pair in $list) { $c = $c.Replace($pair[0], $pair[1]) }
    return $c
}
