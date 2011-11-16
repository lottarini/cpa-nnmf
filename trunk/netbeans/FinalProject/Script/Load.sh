#!/bin/bash

DEFAULT_LOCAL_A=${NMF_HOME}/Data/A
DEFAULT_LOCAL_H=${NMF_HOME}/Data/H
DEFAULT_LOCAL_W=${NMF_HOME}/Data/W
DEFAULT_REMOTE_A=A
DEFAULT_REMOTE_H=H0
DEFAULT_REMOTE_W=W0

${NMF_HOME}/Script/LoadData.sh ${DEFAULT_LOCAL_A} ${DEFAULT_REMOTE_A} ${DEFAULT_LOCAL_H} ${DEFAULT_REMOTE_H} ${DEFAULT_LOCAL_W} ${DEFAULT_REMOTE_W}