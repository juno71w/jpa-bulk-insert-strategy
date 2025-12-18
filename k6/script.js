import http from 'k6/http';
import { check, sleep } from 'k6';

let domain = "host.docker.internal";
export const options = {
    scenarios: {
        test_v1: {
            executor: 'constant-vus',
            vus: 10,
            duration: '30s',
            exec: 'runV1',
            tags: { api_version: 'v1' }, // 결과 태그
        },
        test_v2: {
            executor: 'constant-vus',
            vus: 10,
            duration: '30s',
            exec: 'runV2',
            startTime: '30s',
            tags: { api_version: 'v2' },
        },
        test_v3: {
            executor: 'constant-vus',
            vus: 10,
            duration: '30s',
            exec: 'runV3',
            startTime: '60s',
            tags: { api_version: 'v3' },
        },
    }
};

const base = `http://${domain}:8080/orders`;

export function runV1() {
    request(`${base}/v1/complex-search`, 'v1');
}

export function runV2() {
    request(`${base}/v2/complex-search`, 'v2');
}

export function runV3() {
    request(`${base}/v3/complex-search`, 'v3');
}

function request(url, version) {
    const params =
        '?startDate=2024-01-01T00:00:00&status=COMPLETED&minAmount=100000';

    // 요청에 버전 tag 삽입
    const res = http.get(url + params, { tags: { api_version: version } });

    check(res, { 'status is 200': (r) => r.status === 200 });

    sleep(1);
}