function jump() {
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android？？？？
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios？？？？
    var isMobile = window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i); // 是否手机端


    if ($p("action") == "questionnaire") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=481220&year=' + $p("year") + '&month=' + $p("month") + '&emplid=' + $p("emplid"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=481220&year=' + $p("year") + '&month=' + $p("month") + '&emplid=' + $p("emplid"));
        }
        return;
    } else if ($p("action") == "employeeNotice") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=37622&year=' + $p("year") + '&month=' + $p("month"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=37622&year=' + $p("year") + '&month=' + $p("month"));
        }
        return;
    } else if ($p("action") == "xxxx") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=39121&yggh=' + $p("yggh"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=39121&yggh=' + $p("yggh"));
        }
    } else if ($p("action") == "leaveInfoForManager") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=477221&ry=' + $p("ry") + '&rq=' + $p("rq"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=477221&ry=' + $p("ry") + '&rq=' + $p("rq"));
        }
    } else if ($p("action") == "leaveInfoForCY") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=476720');
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=476720');
        }
    } else if ($p("action") == "leaveInfo") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=477220');
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=477220');
        }
    } else if ($p("action") == "gzdpush") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=481720&id=' + $p("id"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=481720&id=' + $p("id"));
        }
    } else if ($p("action") == "paadc") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=482220&id=' + $p("id"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=482220&id=' + $p("id"));
        }
    } else if ($p("action") == "paadcHrg") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=484220&id=' + $p("id"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=484220&id=' + $p("id"));
        }
    } else if ($p("action") == "paadcT") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=482720&id=' + $p("id"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=482720&id=' + $p("id"));
        }
    } else if ($p("action") == "zzpush") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=484720');
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=484720');
        }
    } else if ($p("action") == "employeeNotice") {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=479220&year=' + $p("year") + '&month=' + $p("month"));
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=479220&year=' + $p("year") + '&month=' + $p("month"));
        }
    } else if ($p("action") == "myPerformance") {//我的业绩
        if (isMobile) {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=8121');//暂时没有url,上线记得改
        } else {
            $u_l_replace('/Shein/oa-pages-pc/storage-performance.html#/MyPerformance');//
        }
    } else if ($p("action") == "teamPerformance") {//团队绩效
        if (isMobile) {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=8121');//暂时没有url,上线记得改
        } else {
            $u_l_replace('/Shein/oa-pages-pc/storage-performance.html#/TeamPerformance');//
        }
    } else if ($p("action") == "directionalEvaluation") {//我的定向评价
        if (isMobile) {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=8121');//暂时没有url,上线记得改
        } else {
            $u_l_replace('/Shein/oa-pages-pc/storage-performance.html#/DirectionalEvaluation');//
        }
    } else {
        if (isiOS) {
            location.replace('/mobilemode/appHomepageView.jsp?appHomepageId=8121');
        } else {
            $u_l_replace('/mobilemode/appHomepageView.jsp?appHomepageId=8121');
        }
    }
}

jump();
