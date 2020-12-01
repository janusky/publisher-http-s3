package app.auditoria;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuditService {

	public void save(AuditVo auditoria) {
		log.info(auditoria.toString());
	}
}
