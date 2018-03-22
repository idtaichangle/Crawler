package com.cvnavi.ship;

import java.util.List;

import com.cvnavi.db.dao.ShipDaoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cvnavi.ais.shipxy.Cmd134;
import com.cvnavi.task.AbstractDailyTask;
import com.cvnavi.task.Schedule;
import com.cvnavi.util.MailSender;

/**
 * 通过shipxy.com抓取船舶资料
 * 
 * @author lixy
 *
 */
public class ShipxyShipInfo extends AbstractDailyTask {
	static Logger log = LogManager.getLogger(ShipxyShipInfo.class);

	Cmd134 cmd = new Cmd134();

	int failCount=0;
	
	@Override
	public Schedule[] initSchedules() {
		return emptySchedules;
	}

	@Override
	public void doTask() {
		List<Ship> list = ShipDaoService.loadShips();
		String mmsi = "";
		for (Ship s : list) {
			mmsi += s.mmsi + ",";
			s.crawled=2;
		}
		if (mmsi.endsWith(",")) {
			mmsi = mmsi.substring(0, mmsi.length() - 1);
		}
		if (mmsi.length() > 0) {
			log.info("Crawle shipxy ship. id from " + list.get(0).id);
			List<com.cvnavi.ais.model.Ship> result = cmd.doCmd(mmsi);
			if (result == null || result.size() == 0) {
				result = cmd.doCmd(mmsi);
			}
			if (result != null && result.size() > 0) {
				failCount=0;
				for (com.cvnavi.ais.model.Ship s0 : result) {
					Ship ship=getShipByMMSI(list,s0.MMSI);
					ship.name_en = s0.Name;
					ship.call_sign = s0.CallSign;
					ship.imo = s0.IMO;
					ship.length = convert(s0.Length);
					ship.breadth = convert(s0.Width);
					ship.draught = convert(s0.Draught);
					ship.type = (int) convert(s0.ShipType);
					ship.typeStr=s0.ShipTypeStr;
					ship.crawled=1;
				}
				ShipDaoService.saveShips(list);
			}else{
				failCount++;
				if(failCount>5){
					log.info("Crawle shipxy ship cancel");
					setScheduleCancel(true);
					MailSender.sendMail("Crawle shipxy ship cancel", " ship id "+ list.get(0).id);					
				}
			}
		}

	}

	private Ship getShipByMMSI(List<Ship> list, String mmsi) {
		for (Ship s : list) {
			if (s.mmsi.equals(mmsi)) {
				return s;
			}
		}
		return null;
	}

	private float convert(String s) {
		s = s.replace("米", "");
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException ex) {
		}
		return 0;
	}

}
