import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 5,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const payload = JSON.stringify({
    titulo: 'Smoke test',
    descricao: 'Criada pelo k6',
    tipo: 'TESTE',
    prioridade: 'ALTA',
  });

  const res = http.post(`${BASE_URL}/adicionar`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, { 'status 201': (r) => r.status === 201 });
  sleep(1);
}