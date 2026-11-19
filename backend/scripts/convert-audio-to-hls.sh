
#!/bin/bash

set -e

INPUT="$1"
OUTPUT="$2"

mkdir -p "$OUTPUT/stream_0"
mkdir -p "$OUTPUT/stream_1"
mkdir -p "$OUTPUT/stream_2"

ffmpeg -y -i "$INPUT" \
-map 0:a:0 -vn -c:a:0 aac -b:a:0 192k -ac:a:0 2 \
-map 0:a:0 -vn -c:a:1 aac -b:a:1 128k -ac:a:1 2 \
-map 0:a:0 -vn -c:a:2 aac -b:a:2 96k -ac:a:2 2 \
-f hls \
-hls_time 5 \
-hls_playlist_type vod \
-hls_segment_type mpegts \
-hls_segment_filename "$OUTPUT/stream_%v/data%03d.ts" \
-master_pl_name master.m3u8 \
-var_stream_map "a:0 a:1 a:2" \
"$OUTPUT/stream_%v/playlist.m3u8"
