#!/bin/bash

set -e

INPUT="$1"
OUTPUT="$2"

mkdir -p "$OUTPUT/1080p"
mkdir -p "$OUTPUT/720p"
mkdir -p "$OUTPUT/480p"

ffmpeg -i "$INPUT" \
-filter_complex \
"[0:v]split=3[v1][v2][v3]; \
[v1]scale=1920:1080[v1out]; \
[v2]scale=1280:720[v2out]; \
[v3]scale=854:480[v3out]" \
-map "[v1out]" -c:v:0 libx264 -b:v:0 5000k -maxrate:v:0 5350k -bufsize:v:0 7500k \
-map "[v2out]" -c:v:1 libx264 -b:v:1 2800k -maxrate:v:1 2996k -bufsize:v:1 4200k \
-map "[v3out]" -c:v:2 libx264 -b:v:2 1400k -maxrate:v:2 1498k -bufsize:v:2 2100k \
-map 0:a:0 -c:a:0 aac -b:a:0 192k -ac:a:0 2 \
-map 0:a:0 -c:a:1 aac -b:a:1 128k -ac:a:1 2 \
-map 0:a:0 -c:a:2 aac -b:a:2 96k -ac:a:2 2 \
-f hls \
-hls_time 5 \
-hls_playlist_type vod \
-hls_flags independent_segments \
-hls_segment_type mpegts \
-hls_segment_filename "$OUTPUT/stream_%v/data%03d.ts" \
-master_pl_name master.m3u8 \
-var_stream_map "v:0,a:0 v:1,a:1 v:2,a:2" \
"$OUTPUT/stream_%v/playlist.m3u8"