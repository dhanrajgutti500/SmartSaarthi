(function(global){
    'use strict';

    function mulberry32(seed){
        return function(){
            seed |= 0; seed = seed + 0x6D2B79F5 | 0;
            var t = Math.imul(seed ^ seed >>> 15, 1 | seed);
            t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t;
            return ((t ^ t >>> 14) >>> 0) / 4294967296;
        };
    }

    var rand = mulberry32(20260808);
    function ri(n){ return Math.floor(rand()*n); }
    function rp(arr){ return arr[ri(arr.length)]; }
    function pad(n, w){ return String(n).padStart(w||2,'0'); }

    var STATES  = ['MH','DL','KA','TN','GJ','UP','RJ','HR','PB','MP','AP','TS','WB','KL','BR','UK','JH','AS'];
    var DISTRICTS = ['01','02','03','04','05','06','07','08','09','10','11','12','13','14'];
    var SERIES  = ['AB','CD','EF','GH','JK','LM','PQ','RS','ST','UV','WX','YZ','BK','CR','DX','EG','FH','GT'];
    var FIRST = ['Rajesh','Amit','Priya','Sunita','Vikram','Anjali','Rahul','Sneha','Sanjay','Pooja','Manoj','Kavita','Arjun','Divya','Sameer','Neha','Rohan','Meera','Ajay','Ishita','Karan','Tina','Deepak','Shreya','Gaurav','Anita','Nikhil','Ritu','Siddharth','Fatima','Rakesh','Suman','Anand','Mona','Rina','Suresh','Lata','Hema','Imran','Kiran'];
    var LAST  = ['Kumar','Sharma','Patel','Iyer','Singh','Gupta','Reddy','Das','Ghosh','Pandey','Chauhan','Mehta','Joshi','Nair','Desai','Kapoor','Malhotra','Agarwal','Verma','Yadav','Khan','Sheikh','Bose','Roy','Jain'];
    var CARS  = ['Maruti Swift','Hyundai i20','Tata Nexon','Mahindra XUV500','Honda City','Toyota Innova','Kia Seltos','Skoda Octavia','Volkswagen Polo','Renault Kwid','Maruti Alto','Hyundai Creta','Tata Harrier','Mahindra Bolero','Honda Amaze','Toyota Fortuner','Maruti Brezza','Hyundai Verna','Tata Tiago','Kia Sonet'];
    var YEARS = ['2016','2017','2018','2019','2020','2021','2022','2023','2024','2025'];
    var FUELS = ['Petrol','Petrol','Petrol','Diesel','Diesel','CNG','LPG','Electric'];
    var INSURERS = ['LIC General Insurance Co.','New India Assurance Co. Ltd','National Insurance Co. Ltd','Oriental Insurance Co. Ltd','United India Insurance Co. Ltd','Bajaj Allianz General Insurance','ICICI Lombard General Insurance','HDFC ERGO General Insurance','IFFCO Tokio General Insurance','TATA AIG General Insurance','Cholamandalam MS General','SBI General Insurance'];
    var PUC_CENTERS = [
        'Mumbai Central PUC Centre (Andheri)','Delhi Gate Emission Testing LLP','Bangalore HSR Auto Check Pvt Ltd','Chennai T Nagar Pollution Lab',
        'Ahmedabad Drive Clean Services','Pune Kothrud Vehicle Care','Hyderabad Jubilee Hills Emission','Kolkata Salt Lake Testing',
        'Jaipur MI Road Vehicle Lab','Lucknow Hazratganj Green Auto','Surat Ring Road Pollution Check','Nagpur Central Emission Lab',
        'Bhopal VIP Road Vehicle Test','Indore MG Road Clean Auto','Patna Gandhi Maidan PUC Center','Vadodara Alkapuri Pollution Lab',
        'Coimbatore Gandhipuram Test','Kochi MG Road Emission','Visakhapatnam MVP Colony','Ludhiana Ferozepur Road Lab'
    ];
    var PLANS = ['Comprehensive Package','Standalone Own Damage','Third Party (Act Only)','Comprehensive (Zero Dep)','Bumper to Bumper Plus','Third Party + PA Cover'];

    function phone(){ return '+91-9'+ String(800000000 + ri(99999999)); }
    function regNumber(seedIdx){
        var st = STATES[seedIdx % STATES.length];
        var ds = DISTRICTS[ri(DISTRICTS.length)];
        var sr = SERIES[seedIdx % SERIES.length];
        var nm = pad(1000 + ri(8999), 4);
        return st+'-'+ds+' '+sr+' '+nm;
    }
    function iso(d){ return d.toISOString().slice(0,10); }
    function dmy(d){ return pad(d.getDate())+' '+d.toLocaleString('en-IN',{month:'short'})+' '+d.getFullYear(); }
    function ddmmyyyy(dateStr){
        var d = new Date(dateStr+'T00:00:00');
        return pad(d.getDate())+'/'+pad(d.getMonth()+1)+'/'+d.getFullYear();
    }
    function addMonths(date, m){ var d = new Date(date); d.setMonth(d.getMonth()+m); return d; }
    function addDays(date, d){ var x = new Date(date); x.setDate(x.getDate()+d); return x; }

    var TODAY = new Date(2026, 7, 6);
    var N = 200;

    function generate(){
        var regUsed = {};
        var VEHICLES = [];
        var POLICIES = [];
        var PUCS = [];
        var CHALLANS = [];

        for (var i = 0; i < N; i++){
            var reg;
            // For first 6 vehicles (demo citizen accounts), use specific registration numbers
            if (i < 6) {
                var demoRegs = ['MH-02-AB-1234', 'MH-02-CD-5678', 'DL-03-EF-9012', 'MH-02-GH-3456', 'KA-04-JK-7890', 'TN-05-LM-8901'];
                reg = demoRegs[i];
            } else {
                do { reg = regNumber(i+ri(17)); } while (regUsed[reg]);
            }
            regUsed[reg] = true;

            var first = rp(FIRST), last = rp(LAST);
            // For first 6 demo citizen accounts, use specific names
            var demoNames = ['Anjali Verma', 'Rahul Sharma', 'Priya Singh', 'Amit Kumar', 'Sneha Patel', 'Sanjay Reddy'];
            var name = (i < 6) ? demoNames[i] : (first+' '+last);
            var fuel  = rp(FUELS);
            var model = rp(CARS);
            var year  = rp(YEARS);
            var carModel = model + ' ' + year;
            // For first 6 demo citizen accounts, use specific phone numbers
            var ownerPhone = (i < 6) ? '+91-987654324' + i : phone();
            var engPrefix = 'EN' + STATES[i % STATES.length] + pad(ri(99),2);
            var engineNo = engPrefix + pad(100000 + ri(899999), 6);
            var chassisNo = 'CH' + pad(ri(99999999),8) + 'MS' + pad(i,3);

            var regYear = 2015 + ri(10);
            var regDate = new Date(regYear, ri(12), 1 + ri(27));

            var vehicle = {
                idx: i,
                reg: reg,
                ownerName: name,
                ownerPhone: ownerPhone,
                fuelType: fuel,
                vehicleModel: carModel,
                makeYear: year,
                engineNo: engineNo,
                chassisNo: chassisNo,
                registrationDateIso: iso(regDate),
                registrationDateDMY: dmy(regDate),
                registrationDateSlash: ddmmyyyy(iso(regDate)),
                vehicleType: (fuel==='Electric'?'Battery Electric Vehicle':(carModel.indexOf('Bolt')>-1||carModel.indexOf('Harrier')>-1||carModel.indexOf('Nexon')>-1 || carModel.indexOf('XUV')>-1 || carModel.indexOf('Fortuner')>-1 || carModel.indexOf('Creta')>-1 || carModel.indexOf('Brezza')>-1 || carModel.indexOf('Sonet')>-1 || carModel.indexOf('Innova')>-1 ? 'Motor Car ('+fuel+')' : (carModel.indexOf('Alto')>-1||carModel.indexOf('Swift')>-1||carModel.indexOf('Polo')>-1||carModel.indexOf('Kwid')>-1||carModel.indexOf('Tiago')>-1 ? 'Motor Car ('+fuel+')' : 'Motor Car ('+fuel+')')))
            };
            VEHICLES.push(vehicle);

            var insurer = rp(INSURERS);
            var plan    = rp(PLANS);
            var tenure  = (ri(100) < 90) ? 1 : (rand()<0.5 ? 2 : 3);
            var idv     = 250000 + ri(18000)*100;
            var tp      = (tenure===1 ? 2094 : (tenure===2 ? 4041 : 5934)) + ri(500);
            var odMin   = Math.round(idv * (fuel==='Electric' ? 0.026 : 0.032));
            var od      = odMin + ri(12000);
            var gst     = Math.round((od+tp) * 0.18);
            var prem    = od + tp + gst;
            var comm    = Math.round(prem * (0.10 + rand()*0.06));
            var rnd = rand();
            var paymentStatus = rnd<0.92 ? 'Paid' : (rnd<0.97 ? 'Pending' : 'Failed');
            var status = (paymentStatus==='Paid') ? 'Issued' : 'Draft';

            var issueDateOffset = ri(330);
            var issueDate = addDays(TODAY, -issueDateOffset);
            var startDate = addDays(issueDate, 1);
            var endDate   = addMonths(startDate, tenure*12); endDate.setDate(endDate.getDate()-1);

            if (status === 'Issued'){
                var daysLeft = Math.round((endDate - TODAY) / (24*36e5));
                if (daysLeft < -7) status = 'Expired';
                else if (daysLeft <= 45 && daysLeft >= 0) status = 'Renewal Due';
            }

            var stAbbr = STATES[i % STATES.length];
            var policySuffix = 'POL-'+stAbbr+pad(TODAY.getMonth()+1,2)+pad(TODAY.getDate(),2);
            var policyNo = (insurer.substring(0,3).toUpperCase().replace(/[^A-Z]/g,'')+'-'+policySuffix+'-'+pad(i+1,5)).replace(/--/g,'-');

            var policy = {
                i: i,
                policyNo: policyNo,
                vehicleReg: reg,
                ownerName: name,
                ownerPhone: ownerPhone,
                insurer: insurer,
                plan: plan,
                tenureMonths: tenure*12,
                tenureYears: tenure,
                idv: idv,
                odPremium: od,
                tpPremium: tp,
                gst: gst,
                totalPremium: prem,
                commission: comm,
                paymentStatus: paymentStatus,
                status: status,
                issueDateIso: iso(issueDate), issueDateDMY: dmy(issueDate), issueDateSlash: ddmmyyyy(iso(issueDate)),
                startDateIso: iso(startDate), startDateDMY: dmy(startDate), startDateSlash: ddmmyyyy(iso(startDate)),
                endDateIso: iso(endDate), endDateDMY: dmy(endDate), endDateSlash: ddmmyyyy(iso(endDate)),
                _daysLeft: Math.round((endDate - TODAY) / (24*36e5))
            };
            POLICIES.push(policy);

            var center = rp(PUC_CENTERS);
            var machId = 'PUC-MACH-'+pad(1000+ri(8999),4);
            var testOffset = ri(360);
            var testDate = addDays(TODAY, -testOffset);
            var validMonths = fuel==='Electric' ? 24 : 12;
            var validDate = addMonths(testDate, validMonths);

            var co = 0, hc = 0, smoke = 0, pucStatus, fee;
            var r = rand();
            if (fuel==='Electric'){
                pucStatus = r<0.98 ? 'Pass' : 'Pending';
                fee = 150;
            } else if (r < 0.80){
                pucStatus = 'Pass';
                if (fuel==='Petrol' || fuel==='LPG'){
                    co = +(0.2 + rand()*0.5).toFixed(2);
                    hc = 40 + ri(50);
                } else {
                    smoke = +(0.3 + rand()*0.8).toFixed(2);
                    co = +(0.1 + rand()*0.2).toFixed(2);
                    hc = 20 + ri(25);
                }
                fee = 200 + ri(150);
            } else if (r < 0.94){
                pucStatus = 'Fail';
                if (fuel==='Petrol' || fuel==='LPG'){
                    co = +(1.2 + rand()*2.3).toFixed(2);
                    hc = 200 + ri(600);
                } else {
                    smoke = +(2.2 + rand()*3.5).toFixed(2);
                    co = +(0.7 + rand()*0.9).toFixed(2);
                    hc = 110 + ri(150);
                }
                fee = 300 + ri(100);
            } else {
                pucStatus = 'Pending';
                co = +(rand()*0.8).toFixed(2);
                hc = 50 + ri(120);
                smoke = +(rand()*1.5).toFixed(2);
                fee = 0;
            }

            if (pucStatus === 'Pass' && validDate < TODAY) pucStatus = 'Expired';
            var expSoon = pucStatus==='Pass' && (Math.round((validDate - TODAY)/(24*36e5)) < 30) && validDate >= TODAY;

            var dist = pad(1+ri(14),2);
            var certNo = 'PUC'+STATES[i%STATES.length]+dist+pad(TODAY.getMonth()+1,2)+pad(i+1,5);

            PUCS.push({
                i: i,
                certNo: certNo,
                reg: reg,
                name: name,
                phone: ownerPhone,
                carModel: carModel,
                center: center,
                mach: machId,
                fuel: fuel,
                testDateIso: iso(testDate), testDate: dmy(testDate),
                validDateIso: iso(validDate), validDate: dmy(validDate),
                co: co, hc: hc, smoke: smoke,
                status: pucStatus,
                fee: fee,
                expiringSoon: expSoon
            });

            // Generate challan data
            // For first 6 demo citizen accounts, always add pending challans
            var hasChallan = (i < 6) ? true : (rand() < 0.25); // Demo accounts always have challans
            if (hasChallan) {
                var challanCount = 1 + (rand() < 0.3 ? 1 : 0); // 1-2 challans
                for (var c = 0; c < challanCount; c++) {
                    var challanTypes = ['No Parking', 'Speeding', 'No Helmet', 'Red Light Jump', 'Wrong Lane', 'Expired Insurance', 'Expired PUC'];
                    var challanType = rp(challanTypes);
                    var fineAmount = 500 + ri(2000);
                    var challanDate = addDays(TODAY, -ri(180));

                    var challanNo = 'CHL' + STATES[i % STATES.length] + pad(TODAY.getFullYear(), 4) + pad(i+1, 6) + pad(c+1, 2);

                    // For first 6 demo citizen accounts, ensure pending challans
                    var isPaid = (i < 6) ? false : (rand() < 0.6); // Demo accounts always have pending challans

                    CHALLANS.push({
                        i: CHALLANS.length,
                        challanNo: challanNo,
                        vehicleReg: reg,
                        ownerName: name,
                        ownerPhone: ownerPhone,
                        violation: challanType,
                        fineAmount: fineAmount,
                        issueDateIso: iso(challanDate),
                        issueDateDMY: dmy(challanDate),
                        status: isPaid ? 'Paid' : 'Pending',
                        location: 'Mumbai',
                        paidDateIso: isPaid ? iso(addDays(challanDate, ri(30))) : null
                    });
                }
            }
        }

        POLICIES.sort(function(a,b){ return b.issueDateIso.localeCompare(a.issueDateIso); });
        PUCS.sort(function(a,b){ return b.testDateIso.localeCompare(a.testDateIso); });
        CHALLANS.sort(function(a,b){ return b.issueDateIso.localeCompare(b.issueDateIso); });

        return {
            VEHICLES: VEHICLES,
            POLICY_DATA: POLICIES,
            PUC_DATA: PUCS,
            CHALLAN_DATA: CHALLANS,
            CONSTANTS: {
                STATES: STATES, FUELS: FUELS, INSURERS: INSURERS,
                PUC_CENTERS: PUC_CENTERS, CARS: CARS, PLANS: PLANS
            }
        };
    }

    var Store = generate();

    Store.getVehicleByReg = function(reg){
        for (var i=0;i<Store.VEHICLES.length;i++){
            if (Store.VEHICLES[i].reg === reg) return Store.VEHICLES[i];
        }
        return null;
    };

    Store.getPolicyByReg = function(reg){
        for (var i=0;i<Store.POLICY_DATA.length;i++){
            if (Store.POLICY_DATA[i].vehicleReg === reg) return Store.POLICY_DATA[i];
        }
        return null;
    };

    Store.getPUCByReg = function(reg){
        for (var i=0;i<Store.PUC_DATA.length;i++){
            if (Store.PUC_DATA[i].reg === reg) return Store.PUC_DATA[i];
        }
        return null;
    };

    Store.getPUCByCert = function(cert){
        for (var i=0;i<Store.PUC_DATA.length;i++){
            if (Store.PUC_DATA[i].certNo === cert) return Store.PUC_DATA[i];
        }
        return null;
    };

    Store.getPolicyByNo = function(no){
        for (var i=0;i<Store.POLICY_DATA.length;i++){
            if (Store.POLICY_DATA[i].policyNo === no) return Store.POLICY_DATA[i];
        }
        return null;
    };

    Store.getChallansByReg = function(reg){
        var normReg = reg.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
        var challans = [];
        for (var i=0;i<Store.CHALLAN_DATA.length;i++){
            var challanReg = Store.CHALLAN_DATA[i].vehicleReg.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
            if (challanReg === normReg) challans.push(Store.CHALLAN_DATA[i]);
        }
        return challans;
    };

    Store.hasPendingChallans = function(reg){
        var normReg = reg.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
        var challans = Store.getChallansByReg(reg);
        for (var i=0;i<challans.length;i++){
            if (challans[i].status === 'Pending') return true;
        }
        return false;
    };

    Store.getPendingChallanCount = function(reg){
        var normReg = reg.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
        var challans = Store.getChallansByReg(reg);
        var count = 0;
        for (var i=0;i<challans.length;i++){
            if (challans[i].status === 'Pending') count++;
        }
        return count;
    };

    global.VehicleData = Store;
})(typeof window !== 'undefined' ? window : this);
