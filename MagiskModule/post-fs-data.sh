#!/system/bin/sh
# Runs at every boot (after /data is mounted)

MODDIR="${MODDIR:-/data/adb/modules/custom-apk-installer}"
LOG="/data/local/tmp/magisk-apkinstall.log"
TMP="/data/local/tmp/apkinstall_splits"

mkdir -p "$MODDIR/apks"

# Clean extraction/output directories
rm -rf "$TMP"

# Log rotation (max 5MB)
if [ -f "$LOG" ]; then
    MAXSIZE=$((5 * 1024 * 1024))  # 5MB
    FILESIZE=$(stat -c%s "$LOG" 2>/dev/null)

    if [ "$FILESIZE" -gt "$MAXSIZE" ]; then
        echo "🔄 Log exceeded limit, rotating..." > "$LOG"
    fi
fi

# ✅ AUTO-INSTALL ON BOOT
if [ "$(ls "$MODDIR/apks"/* 2>/dev/null | wc -l)" -gt 0 ]; then
    echo "📦 Auto-installing queued APKs..." >> "$LOG"
    sh "$MODDIR/customize.sh" >> "$LOG" 2>&1
fi
