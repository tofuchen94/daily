docker login ccr.ccs.tencentyun.com --username=100001626440
cd backend
docker build -t daily-performance:1.0 .  
docker tag daily-performance:1.0 ccr.ccs.tencentyun.com/chendaofu/daily-performance:1.0
docker push ccr.ccs.tencentyun.com/chendaofu/daily-performance:1.0
